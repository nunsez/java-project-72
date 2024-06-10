package hexlet.code.dto.page.url;

import hexlet.code.dto.page.Page;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.util.HttpFlash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UrlPage implements Page {

    @NotNull
    private final Url url;

    @NotNull
    private final List<UrlCheck> urlChecks;

    @Nullable
    private HttpFlash flash;

    public UrlPage(@NotNull Url url) {
        this(url, new ArrayList<>());
    }

    public UrlPage(@NotNull Url url, @Nullable List<UrlCheck> urlChecks) {
        this.url = url;
        this.urlChecks = Objects.requireNonNullElse(urlChecks, new ArrayList<>());
    }

    @NotNull
    public Url url() {
        return url;
    }

    @NotNull
    public List<UrlCheck> urlChecks() {
        return urlChecks;
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
