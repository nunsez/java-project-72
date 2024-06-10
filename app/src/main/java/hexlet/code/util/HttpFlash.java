package hexlet.code.util;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public final class HttpFlash {

    @NotNull
    public static final String FLASH = "flash";

    @NotNull
    public static final String FLASH_TYPE = "flashType";

    public enum Type {
        SUCCESS {
            public String toString() {
                return "success";
            }
        },
        DANGER {
            public String toString() {
                return "danger";
            }
        };
    }

    @NotNull
    private final String message;

    @NotNull
    private final String type;

    private HttpFlash(@NotNull String message, @NotNull Type type) {
        this.message = message;
        this.type = type.toString();
    }

    public static HttpFlash success(@NotNull String message) {
        return new HttpFlash(message, Type.SUCCESS);
    }

    public static HttpFlash danger(@NotNull String message) {
        return new HttpFlash(message, Type.DANGER);
    }

    @NotNull
    public String message() {
        return message;
    }

    @NotNull
    public String type() {
        return type;
    }

    public static HttpFlash consumeFromSession(@NotNull Context context) {
        var message = context.<String>consumeSessionAttribute(FLASH);
        var type = context.<String>consumeSessionAttribute(FLASH_TYPE);

        if (message == null) {
            return null;
        }

        return switch (type) {
            case "success" -> HttpFlash.success(message);
            case "danger" -> HttpFlash.danger(message);
            case null, default -> null;
        };

    }

    public void saveToSession(@NotNull Context context) {
        context.sessionAttribute(FLASH, message());
        context.sessionAttribute(FLASH_TYPE, type());
    }

}
