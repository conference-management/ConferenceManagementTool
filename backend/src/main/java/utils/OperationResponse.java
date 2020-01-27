package utils;

public enum OperationResponse {
    AttendeeSuccess(""),
    AdminSuccess(""),
    InvalidToken(""),
    InvalidArguments("");

    private String message;

    OperationResponse(String message) {
        this.setMessage(message);
    }

    public String getMessage() {
        return this.message;
    }

    ;

    public void setMessage(String message) {
        this.message = message;
    }
}
