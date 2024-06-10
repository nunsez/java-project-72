package hexlet.code.util;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import org.jetbrains.annotations.NotNull;

public class NamedRoutes {

    @NotNull
    public static String rootPath() {
        return "/";
    }

    @NotNull
    public static String urlsPath() {
        return "/urls";
    }

    @NotNull
    public static String urlPath(@NotNull String id) {
        return urlsPath() + "/" + id;
    }

    @NotNull
    public static String urlPath(@NotNull Long id) {
        return urlPath(String.valueOf(id));
    }

    @NotNull
    public static String urlPath(@NotNull Url url) {
        var id = String.valueOf(url.id());
        return urlPath(id);
    }

    @NotNull
    public static String urlChecksPath(@NotNull String urlId) {
        return urlPath(urlId) + "/checks";
    }

    @NotNull
    public static String urlChecksPath(@NotNull Long urlId) {
        return urlChecksPath(String.valueOf(urlId));
    }

    @NotNull
    public static String urlChecksPath(@NotNull Url url) {
        return urlChecksPath(String.valueOf(url.id()));
    }

    @NotNull
    public static String urlCheckPath(@NotNull String urlId, @NotNull String urlCheckId) {
        return urlChecksPath(urlId) + "/" + urlCheckId;
    }

    @NotNull
    public static String urlCheckPath(@NotNull Long urlId, @NotNull Long urlCheckId) {
        return urlCheckPath(String.valueOf(urlId), String.valueOf(urlCheckId));
    }

    @NotNull
    public static String urlCheckPath(@NotNull UrlCheck urlCheck) {
        return urlCheckPath(String.valueOf(urlCheck.urlId()), String.valueOf(urlCheck.id()));
    }

}
