package hexlet.code.service;

import org.jetbrains.annotations.NotNull;

public class ServiceException extends Exception {

    public ServiceException(@NotNull String message) {
        super(message);
    }

}
