package hexlet.code.dto.page.url;

import hexlet.code.dto.page.Page;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.util.HttpFlash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class UrlsPage implements Page {

    @NotNull
    private final List<Url> urls;

    @NotNull
    private final Map<Long, UrlCheck> lastChecks;

    @Nullable
    private HttpFlash flash;

    public UrlsPage(@NotNull List<Url> urls, @NotNull Map<Long, UrlCheck> lastChecks) {
        this.urls = urls;
        this.lastChecks = lastChecks;
    }

    public UrlsPage(@NotNull List<Url> urls) {
        this(urls, Map.of());
    }

    @NotNull
    public List<Url> urls() {
        return urls;
    }

    @Nullable
    public UrlCheck lastCheckForUrl(@NotNull Url url) {
        return lastChecks.get(url.id());
    }

    @Nullable
    @Override
    public HttpFlash flash() {
        return flash;
    }

    @Override
    public void setFlash(@Nullable HttpFlash flash) {
        this.flash = flash;
    }

}
