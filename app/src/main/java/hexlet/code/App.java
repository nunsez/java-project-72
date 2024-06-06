package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.config.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

public class App {
    public static void main(String[] args) throws IOException, SQLException {
        final var app = getApp();
        app.start(getPort());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static final Key<DataSource> DATA_SOURCE = new Key<>("dataSource");

    public static Javalin getApp() throws IOException, SQLException {
        var dataSource = getDataSource();
        initDatabaseSchema(dataSource);

        final var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.useVirtualThreads = true;
            config.appData(DATA_SOURCE, dataSource);
        });

        app.get(NamedRoutes.rootPath(), ctx -> ctx.result("Hello World"));

        return app;
    }

    private static int getPort() {
        final var port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    private static DataSource getDataSource() {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());
        return new HikariDataSource(hikariConfig);
    }

    private static final String DEV_DATABASE_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;";

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", DEV_DATABASE_URL);
    }

    private static void initDatabaseSchema(DataSource dataSource) throws IOException, SQLException {
        var url = App.class.getResource("/schema.sql");
        var path = Objects.requireNonNull(url, "schema.sql not found").getPath();

        var sql = Files.readString(Path.of(path), StandardCharsets.UTF_8);
        LOGGER.info(sql);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.createStatement();
        ) {
            statement.execute(sql);
        }
    }
}
