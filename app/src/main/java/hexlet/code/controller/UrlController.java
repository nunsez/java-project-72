package hexlet.code.controller;

import static io.javalin.rendering.template.TemplateUtil.model;

import hexlet.code.App;
import hexlet.code.dto.page.url.UrlPage;
import hexlet.code.dto.page.url.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.url.AddUrlService;
import hexlet.code.util.NamedRoutes;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public final class UrlController implements CrudHandler {

    private final UrlRepository urlRepository = new UrlRepository(App.DATA_SOURCE);

    @Override
    public void create(@NotNull final Context context) {
        final var rawUrl = context.formParam("url");
        final var result = new AddUrlService(urlRepository).apply(rawUrl);

        result.ifOkOrElse(
            (url) -> context.sessionAttribute("flash", "Страница успешно добавлена"),
            (error) -> context.sessionAttribute("flash", error)
        );

        context.redirect(NamedRoutes.urlsPath());
    }

    @Override
    public void getAll(@NotNull final Context context) {
        List<Url> urls;

        try {
            urls = urlRepository.getEntities();
        } catch (SQLException e) {
            urls = List.of();
        }

        final var flash = context.<String>consumeSessionAttribute("flash");
        final var page = new UrlsPage(urls);
        page.setFlash(flash);

        context.render("url/index.jte", model("page", page));
    }

    @Override
    public void getOne(@NotNull final Context context, @NotNull final String resourceId) {
        Url url;

        try {
            url = urlRepository.find(Long.parseLong(resourceId))
                .orElseThrow(() -> buildNotFoundResponse(resourceId));
        } catch (SQLException e) {
            throw buildNotFoundResponse(resourceId);
        }

        final var page = new UrlPage(url);

        context.render("url/show.jte", model("page", page));
    }

    @Override
    public void update(@NotNull final Context context, @NotNull final String resourceId) {

    }

    @Override
    public void delete(@NotNull final Context context, @NotNull final String resourceId) {

    }

    @NotNull
    private NotFoundResponse buildNotFoundResponse(@NotNull final String resourceId) {
        final var notFoundMessage = "Страница с идентификатором " + resourceId + " не найдена";
        return new NotFoundResponse(notFoundMessage);
    }

}
