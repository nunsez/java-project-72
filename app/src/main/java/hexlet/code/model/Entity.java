package hexlet.code.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Entity {

    @Nullable
    Long id();

    void setId(@NotNull Long id);

}
