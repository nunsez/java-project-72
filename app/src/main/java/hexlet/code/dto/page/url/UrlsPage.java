package hexlet.code.dto.page.url;

import hexlet.code.dto.page.Page;
import hexlet.code.model.Url;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class UrlsPage implements Page {

    @NotNull
    private final List<Url> urls;

    @Nullable
    private String flash;

    public UrlsPage(@NotNull final List<Url> urls) {
        this.urls = urls;
    }

    @Override
    public String flash() {
        return flash;
    }

    @Override
    public void setFlash(@Nullable final String flash) {
        this.flash = flash;
    }

    public List<Url> urls() {
        return urls;
    }

}
