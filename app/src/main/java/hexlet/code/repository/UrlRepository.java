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

    private final DataSource dataSource;

    public static final String TABLE_NAME = "urls";

    public UrlRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @NotNull
    @Override
    public Optional<Url> find(@NotNull final Long id) throws SQLException {
        final var sql = "SELECT * FROM %s WHERE id = ?".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            final var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                final var url = Url.fromResultSet(resultSet);
                return Optional.of(url);
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
            final var sql = "SELECT * from %s".formatted(TABLE_NAME);
            statement.executeQuery(sql);
            final var resultSet = statement.getResultSet();
            final var urls = new ArrayList<Url>();

            while (resultSet.next()) {
                final var url = Url.fromResultSet(resultSet);
                urls.add(url);
            }

            return urls;
        }
    }

    @Override
    public void update(@NotNull final Url entity) throws SQLException {

    }

    @Override
    public void insert(@NotNull final Url entity) throws SQLException {
        final var sql = "INSERT INTO %s (name, inserted_at) VALUES (?, NOW())".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, entity.name());
            statement.executeUpdate();
            final var generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                final var id = generatedKeys.getLong("id");
                syncEntity(id, entity);
            }
        }
    }

    public Optional<Url> findByName(@Nullable final String name) throws SQLException {
        if (name == null) {
            return Optional.empty();
        }

        final var sql = "SELECT * FROM %s WHERE name = ?".formatted(TABLE_NAME);

        try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            final var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(Url.fromResultSet(resultSet));
            }

            return Optional.empty();
        }
    }

    private void syncEntity(@NotNull final Long id, @NotNull final Url entity) throws SQLException {
        final var urlOptional = find(id);

        if (urlOptional.isEmpty()) {
            return;
        }

        final var url = urlOptional.get();

        entity.setId(Objects.requireNonNull(url.id()));
        entity.setName(url.name());
        entity.setInsertedAt(url.insertedAt());
    }

}