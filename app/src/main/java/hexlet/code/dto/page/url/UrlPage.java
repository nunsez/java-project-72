package hexlet.code.dto.page.url;

import hexlet.code.dto.page.Page;
import hexlet.code.model.Url;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UrlPage implements Page {

    @NotNull
    private final Url url;

    @Nullable
    private String flash;

    public UrlPage(@NotNull final Url url) {
        this.url = url;
    }

    @NotNull
    public Url url() {
        return url;
    }

    @Override
    public String flash() {
        return flash;
    }

    @Override
    public void setFlash(@Nullable final String flash) {
        this.flash = flash;
    }

}
