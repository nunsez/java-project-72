package hexlet.code.controller;

import hexlet.code.App;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.ServiceException;
import hexlet.code.service.url_check.AddUrlCheckService;
import hexlet.code.util.HttpFlash;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UrlCheckController {

    @NotNull
    public static final String URL_PARAM = "urlId";

    @NotNull
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(App.DATA_SOURCE);

    @NotNull
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(App.DATA_SOURCE);

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckController.class);

    public static void create(@NotNull Context context) {
        var urlId = context.pathParamAsClass(URL_PARAM, Long.class).get();
        LOGGER.atDebug().log("create param urlId: {}", urlId);

        var service = new AddUrlCheckService(URL_REPOSITORY, URL_CHECK_REPOSITORY);

        try {
            var urlCheck = service.call(urlId);
            LOGGER.atInfo().log("Created UrlCheck: {} {}", urlCheck.id(), urlCheck.title());
            HttpFlash.success("Страница успешно проверена").saveToSession(context);
        } catch (ServiceException e) {
            LOGGER.atError().log("ServiceException: {}", e.getMessage());
            HttpFlash.danger(e.getMessage()).saveToSession(context);
        }

        context.redirect(NamedRoutes.urlPath(urlId));
    }

}
