package communication.packettests;

import communication.wrapper.Connection;

import java.io.File;
import java.util.function.Consumer;

public class TestConnectionWrapper implements Connection {

    private Consumer<String> callback;

    public TestConnectionWrapper(Consumer<String> callback) {
        this.callback = callback;
    }

    @Override
    public void send(String message) {
        callback.accept(message);
    }

    @Override
    public void sendBytes(byte[] bytes) {

    }

    @Override
    public void sendFile(File file) {

    }

    @Override
    public void sendFile(File file, String name) {

    }

    @Override
    public void sendBytes(byte[] bytes, String name) {

    }

    @Override
    public void close() {
        callback.accept("CLOSE");
    }
}
