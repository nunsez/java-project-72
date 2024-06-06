package hexlet.code;

import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        final var app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        final var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.useVirtualThreads = true;
        });

        app.get(NamedRoutes.rootPath(), ctx -> ctx.result("Hello World"));

        return app;
    }

    private static int getPort() {
        final var port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}
