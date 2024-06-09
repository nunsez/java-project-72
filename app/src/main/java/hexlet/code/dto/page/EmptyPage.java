package hexlet.code.dto.page;

import hexlet.code.util.HttpFlash;
import org.jetbrains.annotations.Nullable;

public final class EmptyPage implements Page {

    public EmptyPage() {
    }

    @Nullable
    @Override
    public HttpFlash flash() {
        return null;
    }

    @Override
    public void setFlash(@Nullable HttpFlash flash) {
    }

}
