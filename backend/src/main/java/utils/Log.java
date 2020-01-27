package utils;

import java.util.List;

public interface Log {

    void log(Operation o);

    List<Operation> readLog(int userID);

    List<Operation> readCompleteLog();
}
