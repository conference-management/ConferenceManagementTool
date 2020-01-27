package communication.wrapper;

import java.io.File;

public interface Connection {

    void send(String message);

    void sendBytes(byte[] bytes);

    void sendFile(File file);

    void sendFile(File file, String name);

    void sendBytes(byte[] bytes, String name);

    void close();
}
