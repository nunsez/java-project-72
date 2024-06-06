package hexlet.code.model;

import java.sql.Timestamp;

public final class Url implements Entity {

    private Long id;
    private String name;
    private Timestamp insertedAt;

    public static final String TABLE_NAME = "urls";

    public Url(String name) {
        this.name = name;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp insertedAt() {
        return insertedAt;
    }

}
