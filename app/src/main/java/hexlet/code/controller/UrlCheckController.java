package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.url_check.AddUrlCheckService;
import hexlet.code.util.HttpFlash;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public final class UrlCheckController {

    @NotNull
    public static final String URL_PARAM = "urlId";

    @NotNull
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(App.DATA_SOURCE);

    @NotNull
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(App.DATA_SOURCE);

    public static void create(@NotNull Context context) {
        var urlId = context.pathParamAsClass(URL_PARAM, Long.class).get();
        var result = new AddUrlCheckService(URL_REPOSITORY, URL_CHECK_REPOSITORY).apply(urlId);

        result.ifOkOrElse(
            (urlCheck) -> HttpFlash.success("Страница успешно проверена").saveToSession(context),
            (error) -> HttpFlash.danger(error).saveToSession(context)
        );

        context.redirect(NamedRoutes.urlPath(urlId));
    }

}
