package hexlet.code.repository;

import hexlet.code.model.Entity;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends Entity> {
    DataSource dataSource();

    Optional<T> find(Long id);

    List<T> getEntities();

    void update(T entity);

    void insert(T entity);

    default void save(T entity) {
        var inDatabase = find(entity.id());

        if (inDatabase.isPresent()) {
            update(entity);
        } else {
            insert(entity);
        }
    }
}
