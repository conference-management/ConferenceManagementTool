package request;

import java.util.List;

public interface RequestManagement {

    void addRequest(Request request);

    Request getRequest(int ID);

    List<Request> getAllRequests();
}
