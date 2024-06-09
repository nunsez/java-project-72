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

public class App {

    public static final String JAVA_ENV = System.getenv().getOrDefault("JAVA_ENV", "dev");

    public static void main(String[] args) throws IOException, SQLException {
        final var app = getApp();
        app.start(getPort());
    }

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @NotNull
    public static final DataSource DATA_SOURCE = getDataSource();

    @NotNull
    public static Javalin getApp() throws IOException, SQLException {
        final var dataSource = getDataSource();
        initDatabaseSchema(dataSource);

        final var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.useVirtualThreads = true;
            config.fileRenderer(buildFileRenderer());
        });

        RoutesBuilder.apply(app);

        return app;
    }

    private static int getPort() {
        final var port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    @NotNull
    private static DataSource getDataSource() {
        final var hikariConfig = new HikariConfig();

        // h2database by default
        final var driver = System.getenv().getOrDefault("JDBC_DRIVER", "org.h2.Driver");
        final var jdbcUrl = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");

        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(jdbcUrl);

        return new HikariDataSource(hikariConfig);
    }

    private static void initDatabaseSchema(@NotNull final DataSource dataSource) throws IOException, SQLException {
        final var sql = readResourceFile("/schema.sql");
        LOGGER.info(sql);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.createStatement()
        ) {
            statement.execute(sql);
        }
    }

    @NotNull
    private static String readResourceFile(@NotNull final String filePath) throws IOException {
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
            final var codeResolver = new DirectoryCodeResolver(Path.of("src", "main", "jte"));
            templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        } else {
            templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);
        }

        return new JavalinJte(templateEngine);
    }

}
