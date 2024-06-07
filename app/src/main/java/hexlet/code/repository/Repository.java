package hexlet.code.repository;

import hexlet.code.model.Entity;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends Entity> {

    @NotNull
    Optional<T> find(@NotNull Long id) throws SQLException;

    @NotNull
    List<T> getEntities() throws SQLException;

    void update(@NotNull T entity) throws SQLException;

    void insert(@NotNull T entity) throws SQLException;

    default void save(@NotNull T entity) throws SQLException {
        final var id = entity.id();

        if (id != null && find(id).isPresent()) {
            update(entity);
        } else {
            insert(entity);
        }
    }

}
