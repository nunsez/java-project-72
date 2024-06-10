package hexlet.code.service.url;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;

public final class AddUrlService {

    @NotNull
    private final UrlRepository urlRepository;

    public AddUrlService(@NotNull UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @NotNull
    public Url call(@Nullable String rawUrl) throws ServiceException {
        var urlName = checkMalformed(rawUrl);
        checkExists(urlName);
        var url = new Url(urlName);

        try {
            urlRepository.save(url);
        } catch (SQLException e) {
            throw new ServiceException("Ошибка при добавлении URL");
        }

        return url;
    }

    @NotNull
    private String checkMalformed(@Nullable String rawUrl) throws ServiceException {
        if (rawUrl == null) {
            throw new ServiceException("Некорректный URL");
        }

        try {
            var url = URI.create(rawUrl.strip()).toURL();
            return fromURL(url);
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new ServiceException("Некорректный URL");
        }
    }

    @NotNull
    private String fromURL(@NotNull URL url) {
        var builder = new StringBuilder();

        builder
            .append(url.getProtocol())
            .append("://")
            .append(url.getHost());

        if (url.getPort() > 0) {
            builder.append(url.getPort());
        }

        return builder.toString();
    }

    private void checkExists(@NotNull String name) throws ServiceException {
        Optional<Url> url;

        try {
            url = urlRepository.findByName(name);
        } catch (SQLException e) {
            throw new ServiceException("Ошибка запроса данных");
        }

        if (url.isPresent()) {
            throw new ServiceException("Страница уже существует");
        }
    }

}
