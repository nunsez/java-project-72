package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.App;
import hexlet.code.dto.page.url.UrlPage;
import hexlet.code.dto.page.url.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.ServiceException;
import hexlet.code.service.url.AddUrlService;
import hexlet.code.util.HttpFlash;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.stream.Collectors;

public final class UrlController {

    @NotNull
    public static final String URL_PARAM = "id";

    @NotNull
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(App.DATA_SOURCE);

    @NotNull
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(App.DATA_SOURCE);

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    @NotNull
    private static final String NOT_FOUND_MESSAGE = "Страница с идентификатором %s не найдена";

    public static void create(@NotNull Context context) {
        var rawUrl = context.formParam("url");
        LOGGER.atDebug().log("create param rawUrl: {}", rawUrl);
        var service = new AddUrlService(URL_REPOSITORY);

        try {
            var url = service.call(rawUrl);
            LOGGER.atInfo().log("Url created: {} {}", url.id(), url.name());
            HttpFlash.success("Страница успешно добавлена").saveToSession(context);
        } catch (ServiceException e) {
            LOGGER.atError().log("ServiceException: {}", e.getMessage());
            HttpFlash.danger(e.getMessage()).saveToSession(context);
        }

        context.redirect(NamedRoutes.urlsPath());
    }

    public static void getAll(@NotNull Context context) throws SQLException {
        var urls = URL_REPOSITORY.getEntities().stream()
            .sorted(Comparator.nullsLast(Comparator.comparing(Url::id).reversed()))
            .toList();

        LOGGER.atDebug().setMessage("getAll")
            .addKeyValue("urls", urls.stream().map(Url::name).collect(Collectors.joining(", ")))
            .log();

        var urlIds = urls.stream().map(Url::id).toList();
        var lastChecks = URL_CHECK_REPOSITORY.findLastChecksByUrlIds(urlIds);

        LOGGER.atDebug().setMessage("getAll")
            .addKeyValue("lastChecks", lastChecks.toString())
            .log();

        var page = new UrlsPage(urls, lastChecks);
        var flash = context.<HttpFlash>attribute(HttpFlash.FLASH);
        page.setFlash(flash);

        context.render("url/index.jte", model("page", page));
    }

    public static void getOne(@NotNull Context context) throws SQLException {
        var id = context.pathParamAsClass(URL_PARAM, Long.class).get();

        LOGGER.atDebug().log("getOne id: {}", id);

        var url = URL_REPOSITORY.find(id)
            .orElseThrow(() -> new NotFoundResponse(NOT_FOUND_MESSAGE.formatted(id)));

        LOGGER.atDebug().log("getOne url name: {}", url.name());

        var urlChecks = URL_CHECK_REPOSITORY.findChecksByUrlId(id).stream()
            .sorted(Comparator.nullsLast(Comparator.comparing(UrlCheck::insertedAt).reversed()))
            .toList();

        LOGGER.atDebug().setMessage("getOne")
            .addKeyValue("urlChecks", urlChecks.stream().map(UrlCheck::title).collect(Collectors.joining(", ")))
            .log();

        var page = new UrlPage(url, urlChecks);
        var flash = context.<HttpFlash>attribute(HttpFlash.FLASH);
        page.setFlash(flash);

        context.render("url/show.jte", model("page", page));
    }

}
