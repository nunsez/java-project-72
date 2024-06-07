package hexlet.code.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class Url implements Entity {

    @Nullable
    private Long id;

    @NotNull
    private String name;

    @Nullable
    private Timestamp insertedAt;

    public Url(@NotNull final String name) {
        this.name = name;
    }

    public static Url fromResultSet(@NotNull final ResultSet resultSet) throws SQLException {
        final var id = resultSet.getLong("id");
        final var name = resultSet.getString("name");
        final var insertedAt = resultSet.getTimestamp("inserted_at");

        final var url = new Url(name);
        url.setId(id);
        url.setInsertedAt(insertedAt);

        return url;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(@NotNull final Long id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(@NotNull final String name) {
        this.name = name;
    }

    public Timestamp insertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(@NotNull final Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

}
