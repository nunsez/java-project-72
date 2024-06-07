package hexlet.code.service.url;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

public final class AddUrlService implements Function<String, Result<Url, String>> {

    @NotNull
    private final UrlRepository urlRepository;

    public AddUrlService(@NotNull final UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @NotNull
    @Override
    public Result<Url, String> apply(@Nullable final String rawUrl) {
        return checkMalformed(rawUrl)
            .flatMap(this::checkExists)
            .flatMap(this::save);
    }

    @NotNull
    private Result<String, String> checkMalformed(@Nullable final String rawUrl) {
        if (rawUrl == null) {
            return Result.error("Некорректный адрес");
        }

        try {
            final var url = URI.create(rawUrl).toURL();
            return Result.ok(fromURL(url));
        } catch (IllegalArgumentException | MalformedURLException e) {
            return Result.error("Некорректный адрес");
        }
    }

    @NotNull
    private String fromURL(@NotNull final URL url) {
        final var builder = new StringBuilder();

        builder
            .append(url.getProtocol())
            .append("://")
            .append(url.getHost());

        if (url.getPort() > 0) {
            builder.append(url.getPort());
        }

        return builder.toString();
    }

    @NotNull
    private Result<String, String> checkExists(@NotNull final String name) {
        Optional<Url> url;

        try {
            url = urlRepository.findByName(name);
        } catch (SQLException e) {
            return Result.error("Ошибка запроса данных");
        }

        if (url.isPresent()) {
            return Result.error("Страница уже существует");
        }

        return Result.ok(name);
    }

    @NotNull
    private Result<Url, String> save(@NotNull final String name) {
        var url = new Url(name);

        try {
            urlRepository.save(url);
        } catch (SQLException e) {
            return Result.error("Ошибка при добавлении страницы");
        }

        return Result.ok(url);
    }

}
