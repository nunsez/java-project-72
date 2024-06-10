package hexlet.code.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public final class UrlCheck implements Entity {

    @Nullable
    private Long id;

    private int statusCode;

    @NotNull
    private String title;

    @NotNull
    private String h1;

    @NotNull
    private String description;

    private long urlId;

    @Nullable
    private Timestamp insertedAt;

    public UrlCheck(
        int statusCode,
        @NotNull String title,
        @NotNull String h1,
        @NotNull String description,
        long urlId
    ) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
    }

    public static UrlCheck fromResultSet(@NotNull ResultSet resultSet) throws SQLException {
        var id = resultSet.getLong("id");
        var statusCode = resultSet.getInt("status_code");
        var title = resultSet.getString("title");
        var h1 = resultSet.getString("h1");
        var description = resultSet.getString("description");
        var urlId = resultSet.getLong("url_id");
        var insertedAt = resultSet.getTimestamp("inserted_at");

        var check = new UrlCheck(statusCode, title, h1, description, urlId);
        check.setId(id);
        check.setInsertedAt(insertedAt);

        return check;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public int statusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @NotNull
    public String title() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    @NotNull
    public String h1() {
        return h1;
    }

    public void setH1(@NotNull String h1) {
        this.h1 = h1;
    }

    @NotNull
    public String description() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public long urlId() {
        return urlId;
    }

    public void setUrlId(long urlId) {
        this.urlId = urlId;
    }

    @Nullable
    public Timestamp insertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(@Nullable Timestamp insertedAt) {
        this.insertedAt = insertedAt;
    }

}
