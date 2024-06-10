package hexlet.code.util;

import hexlet.code.model.Url;
import org.jetbrains.annotations.NotNull;

public final class NamedRoutes {

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

}
