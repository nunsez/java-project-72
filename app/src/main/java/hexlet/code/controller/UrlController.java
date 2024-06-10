package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.App;
import hexlet.code.dto.page.url.UrlPage;
import hexlet.code.dto.page.url.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.url.AddUrlService;
import hexlet.code.util.HttpFlash;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class UrlController {

    @NotNull
    public static final String URL_PARAM = "id";

    @NotNull
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(App.DATA_SOURCE);

    @NotNull
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(App.DATA_SOURCE);

    @NotNull
    private static final String NOT_FOUND_MESSAGE = "Страница с идентификатором %s не найдена";

    public static void create(@NotNull final Context context) {
        final var rawUrl = context.formParam("url");
        final var result = new AddUrlService(URL_REPOSITORY).apply(rawUrl);

        result.ifOkOrElse(
            (url) -> HttpFlash.success("Страница успешно добавлена").saveToSession(context),
            (error) -> HttpFlash.danger(error).saveToSession(context)
        );

        context.redirect(NamedRoutes.urlsPath());
    }

    public static void getAll(@NotNull final Context context) {
        final List<Url> urls;

        try {
            urls = URL_REPOSITORY.getEntities().stream()
                .sorted(Comparator.nullsLast(Comparator.comparing(Url::id).reversed()))
                .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final Map<Long, UrlCheck> lastChecks;

        try {
            var urlIds = urls.stream().map(Url::id).toList();
            lastChecks = URL_CHECK_REPOSITORY.findLastChecksByUrlIds(urlIds);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final var page = new UrlsPage(urls, lastChecks);
        final var flash = context.<HttpFlash>attribute(HttpFlash.FLASH);
        page.setFlash(flash);

        context.render("url/index.jte", model("page", page));
    }

    public static void getOne(@NotNull final Context context) {
        final var id = context.pathParamAsClass(URL_PARAM, Long.class).get();
        Url url;

        try {
            url = URL_REPOSITORY.find(id)
                .orElseThrow(() -> new NotFoundResponse(NOT_FOUND_MESSAGE.formatted(id)));
        } catch (SQLException e) {
            throw new NotFoundResponse(NOT_FOUND_MESSAGE.formatted(id));
        }

        List<UrlCheck> urlChecks;

        try {
            urlChecks = URL_CHECK_REPOSITORY.findChecksByUrlId(id).stream()
                .sorted(Comparator.nullsLast(Comparator.comparing(UrlCheck::insertedAt).reversed()))
                .toList();
        } catch (SQLException e) {
            urlChecks = List.of();
        }

        final var page = new UrlPage(url, urlChecks);
        final var flash = context.<HttpFlash>attribute(HttpFlash.FLASH);
        page.setFlash(flash);

        context.render("url/show.jte", model("page", page));
    }

}
