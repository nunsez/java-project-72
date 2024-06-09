package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class UrlCheckControllerTest {

    private Javalin app;

    private UrlRepository urlRepository;

    private UrlCheckRepository urlCheckRepository;

    @BeforeEach
    public void setup() throws IOException, SQLException {
        app = App.getApp();
        urlCheckRepository = new UrlCheckRepository(App.DATA_SOURCE);
        urlRepository = new UrlRepository(App.DATA_SOURCE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://example.com"})
    public void createOk(String input) throws SQLException {
        final var url = new Url(input);
        urlRepository.save(url);
        final var urlId = Objects.requireNonNull(url.id());

        JavalinTest.test(app, (server, client) -> {
            var urlChecks = urlCheckRepository.findChecksByUrlId(urlId);
            urlChecks = urlCheckRepository.findChecksByUrlId(urlId);
            assertThat(urlChecks).isEmpty();

            try (var response = client.post(NamedRoutes.urlChecksPath(url))) {
                assertThat(response.code()).isEqualTo(200);
                urlChecks = urlCheckRepository.findChecksByUrlId(urlId);
                assertThat(urlChecks).hasSize(1);
                assertThat(Objects.requireNonNull(response.body()).string()).contains("200");
            }

            try (var response = client.post(NamedRoutes.urlChecksPath(url))) {
                assertThat(response.code()).isEqualTo(200);
                urlChecks = urlCheckRepository.findChecksByUrlId(urlId);
                assertThat(urlChecks).hasSize(2);
            }
        });

        assertThat(urlRepository.findByName(input)).isPresent();
    }

    @Test
    public void createInvalid() {

    }
}
