package logicalguess.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Holder<V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Holder.class);

    private Supplier<V> creator;
    private Supplier<V> factory = () -> {throw new IllegalStateException("init() needs to be called first");};


    public Holder(Supplier<V> creator) {
        this.creator = creator;
    }

    public V get() {
        return factory.get();
    }

    public synchronized V init() {
        factory = new Factory();
        return factory.get();
    }

    private class Factory implements Supplier<V> {
        private final V instance = creator.get();

        @Override
        public V get() {
            return instance;
        }
    }

    public static void main(String[] args) {
        Holder<String> h = new Holder<>(() -> "cool");
        h.init();
        LOGGER.debug(h.get());
    }
}
