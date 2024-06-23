package pw.kaboom.extras.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {
    private @Nullable T value;
    private final Supplier<T> supplier;

    public Lazy(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.value == null) {
            this.value = this.supplier.get();
        }

        return this.value;
    }
}
