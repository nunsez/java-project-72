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

    public Url(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public static Url fromResultSet(@NotNull ResultSet resultSet) throws SQLException {
        var id = resultSet.getLong("id");
        var name = resultSet.getString("name");
        var insertedAt = resultSet.getTimestamp("created_at");

        var url = new Url(name);
        url.setId(id);
        url.setInsertedAt(insertedAt);

        return url;
    }

    @Nullable
    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String name() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public Timestamp insertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(@NotNull Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

}
