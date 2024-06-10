package hexlet.code;

import hexlet.code.controller.UrlCheckController;
import hexlet.code.controller.UrlController;
import hexlet.code.util.HttpFlash;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

public final class RoutesBuilder {

    public static void apply(@NotNull Javalin app) {
        app.before(context -> context.contentType("text/plain;charset=utf-8"));

        handleFlash(app);

        app.get(NamedRoutes.rootPath(), context -> context.render("index.jte"));

        app.post(NamedRoutes.urlsPath(), UrlController::create);
        app.get(NamedRoutes.urlsPath(), UrlController::getAll);
        app.get(NamedRoutes.urlPath(pattern(UrlController.URL_PARAM)), UrlController::getOne);

        app.post(NamedRoutes.urlChecksPath(pattern(UrlCheckController.URL_PARAM)), UrlCheckController::create);
    }

    private static void handleFlash(@NotNull Javalin app) {
        app.before(context -> {
            var flash = HttpFlash.consumeFromSession(context);

            if (flash != null) {
                context.attribute(HttpFlash.FLASH, flash);
            }
        });
    }

    @NotNull
    private static String pattern(@NotNull String param) {
        return "{" + param + "}";
    }

}
