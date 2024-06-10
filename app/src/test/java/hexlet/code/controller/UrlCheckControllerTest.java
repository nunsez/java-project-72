package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.dto.page.EmptyPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UrlCheckControllerTest {

    private Javalin app;

    private MockWebServer mockWebServer;

    private UrlRepository urlRepository;

    private UrlCheckRepository urlCheckRepository;

    @BeforeEach
    public void beforeEach() throws IOException, SQLException {
        app = App.getApp();
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        urlCheckRepository = new UrlCheckRepository(App.DATA_SOURCE);
        urlRepository = new UrlRepository(App.DATA_SOURCE);
    }

    @AfterEach
    public void afterEach() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void createOk() throws SQLException {
        var html1 = getHtml("title1", "desc1", "h1/1");
        mockWebServer.enqueue(new MockResponse().setBody(html1));

        var html2 = getHtml("title2", "desc2", "h1/2");
        mockWebServer.enqueue(new MockResponse().setBody(html2));

        var urlName = mockWebServer.url("/").toString();
        var urlId = saveUrlWithName(urlName);

        // Проверяем, что до начала запросов список проверок пуст
        var urlChecks0 = urlCheckRepository.findChecksByUrlId(urlId);
        assertThat(urlChecks0).isEmpty();

        JavalinTest.test(app, (server, client) -> {
            // Проверяем, что была добавлена первая проверка
            try (var response1 = client.post(NamedRoutes.urlChecksPath(urlId))) {
                assertThat(response1.code()).isEqualTo(200);

                var urlChecks1 = urlCheckRepository.findChecksByUrlId(urlId);
                assertThat(urlChecks1).hasSize(1);

                assertThat(response1.body().string())
                    .contains("<td>200</td>")
                    .contains("title1")
                    .contains("desc1")
                    .contains("h1/1");
            }

            // Проверяем, что после повторного запроса на проверку у нас теперь 2 проверки в списке
            try (var response2 = client.post(NamedRoutes.urlChecksPath(urlId))) {
                assertThat(response2.code()).isEqualTo(200);

                var urlChecks2 = urlCheckRepository.findChecksByUrlId(urlId);
                assertThat(urlChecks2).hasSize(2);

                assertThat(response2.body().string())
                    .contains("title2")
                    .contains("desc2")
                    .contains("h1/2");
            }

            // Проверяем, что на главной странице именно последняя проверка
            var response3 = client.get(NamedRoutes.urlsPath());
            var lastCheck = urlCheckRepository.findLastChecksByUrlIds(List.of(urlId)).get(urlId);
            assertThat(response3.code()).isEqualTo(200);
            var dateTime = new EmptyPage().formatTimestamp(lastCheck.insertedAt());
            assertThat(response3.body().string()).contains(dateTime);
        });

        // Проверяем, что после всех запросов в базе осталась ссылка
        assertThat(urlRepository.findByName(urlName)).isPresent();
    }

    @Test
    public void createBadRequest() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));
        var urlName = mockWebServer.url("/").toString();

        JavalinTest.test(app, (server, client) -> {
            var urlId = saveUrlWithName(urlName);

            // Проверяем, что сервис корректно обрабатывает не 200 коды ответа
            try (var response = client.post(NamedRoutes.urlChecksPath(urlId))) {
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string()).contains("<td>400</td>");
            }
        });
    }

    @Test
    public void createUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            // Проверяем, что при запросе на несуществующий адрес мы получаем 404
            try (var response = client.post(NamedRoutes.urlChecksPath("777"))) {
                assertThat(response.code()).isEqualTo(404);
            }
        });
    }

    @Test
    public void createInvalidHtml() {
        mockWebServer.enqueue(new MockResponse().setBody("invalid html"));
        var urlName = mockWebServer.url("/").toString();

        JavalinTest.test(app, (server, client) -> {
            var urlId = saveUrlWithName(urlName);

            // Проверяем случай некорректного HTML в ответе по ссылке
            try (var response = client.post(NamedRoutes.urlChecksPath(urlId))) {
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string()).contains("1");
            }
        });
    }

    private long saveUrlWithName(String urlName) throws SQLException {
        var url = new Url(urlName);
        urlRepository.save(url);
        return url.id();
    }

    private static final String HTML = """
        <html>
        <head>
            <meta charset="utf-8"/>
            <title>%s</title>
            <meta name="description" content="%s"
        </head>
        <body>
            <main><h1>%s</h1></main>
        </body>
        </html>
        """;

    private static String getHtml(String title, String description, String h1) {
        return HTML.formatted(title, description, h1);
    }

}
