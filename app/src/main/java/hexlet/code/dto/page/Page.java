package hexlet.code.dto.page;

import hexlet.code.util.HttpFlash;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public interface Page {

    @Nullable
    HttpFlash flash();

    void setFlash(@Nullable HttpFlash flash);

    @NotNull
    default String formatTimestamp(@Nullable Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }

        final var formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(timestamp);
    }

}
