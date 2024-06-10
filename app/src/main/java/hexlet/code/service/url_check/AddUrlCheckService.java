package hexlet.code.service.url_check;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.Result;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public final class AddUrlCheckService implements Function<Long, Result<UrlCheck, String>> {

    @NotNull
    private final UrlRepository urlRepository;

    @NotNull
    private final UrlCheckRepository urlCheckRepository;

    public AddUrlCheckService(
        @NotNull final UrlRepository urlRepository,
        @NotNull final UrlCheckRepository urlCheckRepository
    ) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    public Result<UrlCheck, String> apply(@NotNull final Long urlId) {
        final var statusCode = new AtomicReference<Integer>();

        return getUrl(urlId)
            .flatMap(this::getResponse)
            .flatMap(response -> {
                statusCode.set(response.getStatus());
                return parseDoc(response);
            })
            .map(doc -> buildUrlCheck(urlId, statusCode.get(), doc))
            .flatMap(this::save);
    }

    private Result<Url, String> getUrl(@NotNull final Long id) {
        final Optional<Url> url;

        try {
            url = urlRepository.find(id);
        } catch (SQLException e) {
            return Result.error("Ошибка запроса данных");
        }

        return url.<Result<Url, String>>map(Result::ok)
            .orElseGet(() -> Result.error("Страница не найдена"));
    }

    private Result<HttpResponse<String>, String> getResponse(Url url) {
        final var response = Unirest.get(url.name()).asString();
        return Result.ok(response);
    }

    private Result<Document, String> parseDoc(HttpResponse<String> response) {
        final var doc = Jsoup.parse(response.getBody());
        return Result.ok(doc);
    }

    private UrlCheck buildUrlCheck(Long urlId, Integer statusCode, Document doc) {
        final var h1Node = doc.select("h1").first();
        final var descriptionNode = doc.select("head meta[name='description']").first();

        return new UrlCheck(
            statusCode,
            doc.title(),
            h1Node == null ? "" : h1Node.text(),
            descriptionNode == null ? "" : descriptionNode.attr("content"),
            urlId
        );
    }

    private Result<UrlCheck, String> save(@NotNull final UrlCheck urlCheck) {
        try {
            urlCheckRepository.save(urlCheck);
        } catch (SQLException e) {
            return Result.error("Ошибка при добавлении проверки");
        }

        return Result.ok(urlCheck);
    }

}
