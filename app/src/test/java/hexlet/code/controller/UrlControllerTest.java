package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


class UrlControllerTest {

    private Javalin app;

    private UrlRepository urlRepository;

    @BeforeEach
    public void beforeEach() throws IOException, SQLException {
        app = App.getApp();
        urlRepository = new UrlRepository(App.DATA_SOURCE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com"})
    public void createOk(String input) {
        var body = "url=" + input;

        JavalinTest.test(app, (server, client) -> {
            try (var response = client.post(NamedRoutes.urlsPath(), body)) {
                assertThat(response.code()).isEqualTo(200);
                assertThat(urlRepository.findByName(input)).isPresent();
            }
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid%20url"})
    public void createInvalid(String input) {
        var body = "url=" + input;

        JavalinTest.test(app, (server, client) -> {
            try (var response = client.post(NamedRoutes.urlsPath(), body)) {
                assertThat(response.code()).isEqualTo(200);
                assertThat(urlRepository.findByName(input)).isEmpty();
            }
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com"})
    public void createDuplicate(String input) {
        var body = "url=" + input;

        JavalinTest.test(app, (server, client) -> {
            try (var response = client.post(NamedRoutes.urlsPath(), body)) {
                assertThat(response.code()).isEqualTo(200);
                assertThat(urlRepository.findByName(input)).isPresent();
            }

            try (var response = client.post(NamedRoutes.urlsPath(), body)) {
                assertThat(response.code()).isEqualTo(200);
            }
        });
    }

    @Test
    public void getAllEmpty() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com"})
    public void getAllNotEmpty(String input) throws SQLException {
        urlRepository.save(new Url(input));

        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(input);
        });
    }

    @Test
    public void getOneNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("777"));
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com"})
    public void getOneOk(String input) throws SQLException {
        var url = new Url(input);
        urlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(url.name());
        });
    }

}
