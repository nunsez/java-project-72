package hexlet.code.util;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public final class HttpFlash {

    @NotNull
    public static final String FLASH = "flash";

    @NotNull
    public static final String FLASH_TYPE = "flash-type";

    public enum Type {
        SUCCESS {
            public String toString() {
                return "success";
            }
        },
        ERROR {
            public String toString() {
                return "error";
            }
        };
    }

    @NotNull
    private final String message;

    @NotNull
    private final String type;

    private HttpFlash(@NotNull final String message, @NotNull final Type type) {
        this.message = message;
        this.type = type.toString();
    }

    public static HttpFlash success(@NotNull final String message) {
        return new HttpFlash(message, Type.SUCCESS);
    }

    public static HttpFlash error(@NotNull final String message) {
        return new HttpFlash(message, Type.ERROR);
    }

    @NotNull
    public String message() {
        return message;
    }

    @NotNull
    public String type() {
        return type;
    }

    public static HttpFlash consumeFromSession(@NotNull final Context context) {
        final var message = context.<String>consumeSessionAttribute(FLASH);
        final var type = context.<String>consumeSessionAttribute(FLASH_TYPE);

        if (message == null) {
            return null;
        }

        return switch (type) {
            case "success" -> HttpFlash.success(message);
            case "error" -> HttpFlash.error(message);
            case null, default -> null;
        };

    }

    public void saveToSession(@NotNull final Context context) {
        context.sessionAttribute(FLASH, message());
        context.sessionAttribute(FLASH_TYPE, type());
    }

}
