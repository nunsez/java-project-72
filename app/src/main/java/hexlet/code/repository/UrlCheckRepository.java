package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class UrlCheckRepository implements Repository<UrlCheck> {

    @NotNull
    private final DataSource dataSource;

    @NotNull
    public static final String TABLE_NAME = "url_checks";

    public UrlCheckRepository(@NotNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @NotNull
    @Override
    public Optional<UrlCheck> find(@NotNull Long id) throws SQLException {
        var sql = "SELECT * FROM %s WHERE id = ?".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                var entity = UrlCheck.fromResultSet(resultSet);
                return Optional.of(entity);
            }

            return Optional.empty();
        }
    }

    @NotNull
    @Override
    public List<UrlCheck> getEntities() throws SQLException {
        try (
            var connection = dataSource.getConnection();
            var statement = connection.createStatement()
        ) {
            var sql = "SELECT * FROM %s".formatted(TABLE_NAME);
            statement.executeQuery(sql);
            var resultSet = statement.getResultSet();
            var entities = new ArrayList<UrlCheck>();

            while (resultSet.next()) {
                var entity = UrlCheck.fromResultSet(resultSet);
                entities.add(entity);
            }

            return entities;
        }
    }

    @Override
    public void update(@NotNull UrlCheck entity) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void insert(@NotNull UrlCheck entity) throws SQLException {
        var sql = """
            INSERT INTO %s (status_code, title, h1, description, url_id)
            VALUES (?, ?, ?, ?, ?)
            """.formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, entity.statusCode());
            statement.setString(2, entity.title());
            statement.setString(3, entity.h1());
            statement.setString(4, entity.description());
            statement.setLong(5, entity.urlId());
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                var id = generatedKeys.getLong("id");
                syncEntity(id, entity);
            }
        }
    }

    @NotNull
    public List<UrlCheck> findChecksByUrlId(@NotNull Long urlId) throws SQLException {
        var sql = """
            SELECT *
            FROM %s
            WHERE url_id = ?
            """.formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, urlId);
            var resultSet = statement.executeQuery();
            var entities = new ArrayList<UrlCheck>();

            while (resultSet.next()) {
                var entity = UrlCheck.fromResultSet(resultSet);
                entities.add(entity);
            }

            return entities;
        }
    }

    @NotNull
    public Map<Long, UrlCheck> findLastChecksByUrlIds(@NotNull List<Long> urlIds) throws SQLException {
        if (urlIds.isEmpty()) {
            return new HashMap<>();
        }

        var sql = lastCheckSql(urlIds.size());

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql);
        ) {
            var index = 1;
            for (var urlId : urlIds) {
                statement.setLong(index, urlId);
                index += 1;
            }

            var resultSet = statement.executeQuery();
            var entities = new HashMap<Long, UrlCheck>();

            while (resultSet.next()) {
                var entity = UrlCheck.fromResultSet(resultSet);
                entities.put(entity.urlId(), entity);
            }

            return entities;
        }
    }

    @NotNull
    private static String lastCheckSql(int size) {
        var placeholder = IntStream.range(0, size)
            .mapToObj(i -> "?")
            .collect(Collectors.joining(","));

        return """
            WITH last_checks AS (
                SELECT DISTINCT ON (url_id)
                    url_id,
                    id AS check_id,
                    inserted_at
                FROM %s
                GROUP by url_id, check_id
                order by url_id, inserted_at DESC
            )

            SELECT *
            FROM %s
            WHERE id IN (
                SELECT check_id
                FROM last_checks
                WHERE url_id IN (%s)
            )""".formatted(TABLE_NAME, TABLE_NAME, placeholder);
    }

    private void syncEntity(@NotNull Long id, @NotNull UrlCheck urlCheck) throws SQLException {
        var entityOptional = find(id);

        if (entityOptional.isEmpty()) {
            return;
        }

        var entity = entityOptional.get();

        urlCheck.setId(id);
        urlCheck.setStatusCode(entity.statusCode());
        urlCheck.setTitle(entity.title());
        urlCheck.setH1(entity.h1());
        urlCheck.setDescription(entity.description());
        urlCheck.setUrlId(entity.urlId());
        urlCheck.setInsertedAt(entity.insertedAt());
    }

}
