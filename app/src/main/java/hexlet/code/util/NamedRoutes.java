package hexlet.code.util;

import hexlet.code.model.Url;
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
    public static String urlPath(@NotNull final String id) {
        return "/urls/" + id;
    }

    @NotNull
    public static String urlPath(@NotNull final Long id) {
        return urlPath(String.valueOf(id));
    }

    @NotNull
    public static String urlPath(@NotNull final Url url) {
        var id = String.valueOf(url.id());
        return urlPath(id);
    }

}
