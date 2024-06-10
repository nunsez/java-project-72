package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;

import io.javalin.rendering.FileRenderer;
import io.javalin.rendering.template.JavalinJte;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;

public final class App {

    @NotNull
    public static final String JAVA_ENV = System.getenv().getOrDefault("JAVA_ENV", "dev");

    public static void main(@NotNull String[] args) throws IOException, SQLException {
        var app = getApp();
        app.start(getPort());
    }

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @NotNull
    public static final DataSource DATA_SOURCE = getDataSource();

    @NotNull
    public static Javalin getApp() throws IOException, SQLException {
        var dataSource = getDataSource();
        initDatabaseSchema(dataSource);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.useVirtualThreads = true;
            config.fileRenderer(buildFileRenderer());
        });

        RoutesBuilder.apply(app);

        return app;
    }

    private static int getPort() {
        var port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    @NotNull
    private static DataSource getDataSource() {
        var hikariConfig = new HikariConfig();

        // h2database by default
        var jdbcUrl = System.getenv()
            .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        hikariConfig.setJdbcUrl(jdbcUrl);

        if (jdbcUrl.startsWith("jdbc:h2")) {
            hikariConfig.setDriverClassName("org.h2.Driver");
        } else if (jdbcUrl.startsWith("jdbc:postgresql")) {
            hikariConfig.setDriverClassName("org.postgresql.Driver");
        } else {
            var driver = System.getenv("JDBC_DRIVER");
            if (driver != null) {
                hikariConfig.setDriverClassName(driver);
            }
        }

        return new HikariDataSource(hikariConfig);
    }

    private static void initDatabaseSchema(@NotNull DataSource dataSource) throws IOException, SQLException {
        var sql = readResourceFile("/schema.sql");
        LOGGER.info(sql);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.createStatement()
        ) {
            statement.execute(sql);
        }
    }

    @NotNull
    private static String readResourceFile(@NotNull String filePath) throws IOException {
        try (
            var inputStream = Objects.requireNonNull(
                App.class.getResourceAsStream(filePath),
                "Resource not found: " + filePath
            );
            var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            var reader = new BufferedReader(inputStreamReader)
        ) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    @NotNull
    private static FileRenderer buildFileRenderer() {
        final TemplateEngine templateEngine;

        if (JAVA_ENV.equals("dev")) {
            var codeResolver = new DirectoryCodeResolver(Path.of("src", "main", "jte"));
            templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        } else {
            templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);
        }

        return new JavalinJte(templateEngine);
    }

}
