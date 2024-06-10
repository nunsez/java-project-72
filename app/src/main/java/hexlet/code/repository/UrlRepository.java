package hexlet.code.repository;

import hexlet.code.model.Url;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class UrlRepository implements Repository<Url> {

    @NotNull
    private final DataSource dataSource;

    @NotNull
    public static final String TABLE_NAME = "urls";

    public UrlRepository(@NotNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @NotNull
    @Override
    public Optional<Url> find(@NotNull Long id) throws SQLException {
        var sql = "SELECT * FROM %s WHERE id = ?".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                var entity = Url.fromResultSet(resultSet);
                return Optional.of(entity);
            }

            return Optional.empty();
        }
    }

    @NotNull
    @Override
    public List<Url> getEntities() throws SQLException {
        try (
            var connection = dataSource.getConnection();
            var statement = connection.createStatement()
        ) {
            var sql = "SELECT * FROM %s".formatted(TABLE_NAME);
            statement.executeQuery(sql);
            var resultSet = statement.getResultSet();
            var entities = new ArrayList<Url>();

            while (resultSet.next()) {
                var entity = Url.fromResultSet(resultSet);
                entities.add(entity);
            }

            return entities;
        }
    }

    @Override
    public void update(@NotNull Url entity) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void insert(@NotNull Url entity) throws SQLException {
        var sql = "INSERT INTO %s (name) VALUES (?)".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, entity.name());
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                var id = generatedKeys.getLong("id");
                syncEntity(id, entity);
            }
        }
    }

    @NotNull
    public Optional<Url> findByName(@Nullable String name) throws SQLException {
        if (name == null) {
            return Optional.empty();
        }

        var sql = "SELECT * FROM %s WHERE name = ?".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Url.fromResultSet(resultSet));
            }

            return Optional.empty();
        }
    }

    private void syncEntity(@NotNull Long id, @NotNull Url url) throws SQLException {
        var entityOptional = find(id);

        if (entityOptional.isEmpty()) {
            return;
        }

        var entity = entityOptional.get();

        url.setId(Objects.requireNonNull(entity.id()));
        url.setName(entity.name());
        url.setInsertedAt(Objects.requireNonNull(entity.insertedAt(), "must be set by database"));
    }

}
