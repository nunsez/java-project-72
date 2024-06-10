package hexlet.code.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Result<T, E> {

    @Nullable
    private final T data;

    @Nullable
    private final E error;

    private Result(@Nullable T data, @Nullable E error) {
        this.data = data;
        this.error = error;
    }

    @NotNull
    public static <T, E> Result<T, E> ok(@NotNull T data) {
        return new Result<>(data, null);
    }

    @NotNull
    public static <T, E> Result<T, E> error(@NotNull E error) {
        return new Result<>(null, error);
    }

    @NotNull
    public T data() {
        return Objects.requireNonNull(data);
    }

    @NotNull
    public E error() {
        return Objects.requireNonNull(error);
    }

    public boolean isOk() {
        return Objects.isNull(error);
    }

    public boolean isError() {
        return !isOk();
    }

    @NotNull
    public <U> Result<U, E> map(@NotNull Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isOk()) {
            var newData = mapper.apply(data());
            return Result.ok(Objects.requireNonNull(newData));
        } else {
            return Result.error(error());
        }
    }

    @NotNull
    public <U> Result<U, E> flatMap(@NotNull Function<? super T, ? extends Result<U, E>> mapper) {
        Objects.requireNonNull(mapper);
        if (isOk()) {
            var newResult = Objects.requireNonNull(mapper.apply(data()));
            if (newResult.isOk()) {
                return newResult;
            }
            return Result.error(newResult.error());
        }
        return Result.error(error());
    }

    @NotNull
    public T orElse(T other) {
        return isOk() ? data() : other;
    }

    public void ifOkOrElse(
        @NotNull Consumer<? super T> okAction,
        @NotNull Consumer<? super E> errorAction
    ) {
        if (isOk()) {
            okAction.accept(data());
        } else {
            errorAction.accept(error());
        }
    }

}
