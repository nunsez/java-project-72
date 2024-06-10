package hexlet.code.service.url_check;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.ServiceException;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;

public final class AddUrlCheckService {

    @NotNull
    private final UrlRepository urlRepository;

    @NotNull
    private final UrlCheckRepository urlCheckRepository;

    public AddUrlCheckService(
        @NotNull UrlRepository urlRepository,
        @NotNull UrlCheckRepository urlCheckRepository
    ) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    @NotNull
    public UrlCheck call(@NotNull Long urlId) throws ServiceException {
        var url = getUrl(urlId);
        var response = Unirest.get(url.name()).asString();
        var statusCode = response.getStatus();
        var doc = Jsoup.parse(response.getBody());
        var urlCheck = buildUrlCheck(urlId, statusCode, doc);
        save(urlCheck);
        return urlCheck;
    }

    @NotNull
    private Url getUrl(@NotNull Long id) throws ServiceException {
        try {
            return urlRepository.find(id)
                .orElseThrow(() -> new ServiceException("Страница не найдена"));
        } catch (SQLException e) {
            throw new ServiceException("Ошибка запроса данных");
        }
    }

    @NotNull
    private UrlCheck buildUrlCheck(@NotNull Long urlId, @NotNull Integer statusCode, @NotNull Document doc) {
        var h1Node = doc.select("h1").first();
        var descriptionNode = doc.select("head meta[name='description']").first();

        return new UrlCheck(
            statusCode,
            doc.title(),
            h1Node == null ? "" : h1Node.text(),
            descriptionNode == null ? "" : descriptionNode.attr("content"),
            urlId
        );
    }

    private void save(@NotNull UrlCheck urlCheck) throws ServiceException {
        try {
            urlCheckRepository.save(urlCheck);
        } catch (SQLException e) {
            throw new ServiceException("Ошибка при добавлении проверки");
        }
    }

}
