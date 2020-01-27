package request;

import com.google.gson.annotations.Expose;

public class SimpleRequestable implements Requestable { // used for frontend communication

    @Expose
    private String name;

    public SimpleRequestable(String name) {
        this.name = name;
    }

    @Override
    public String getRequestableName() {
        return name;
    }
}
