package hexlet.code.dto.page;

import org.jetbrains.annotations.Nullable;

public interface Page {

    @Nullable
    String flash();

    void setFlash(@Nullable String flash);

}
