package user;

/**
 * A simple data representation of an {@link Attendee} used for JSON parsing.
 */
public class SimpleAttendee {
    private String name;
    private String email;
    private String group;
    private String residence;
    private String function;

    public SimpleAttendee(String name, String email, String group, String residence, String function) {
        this.name = name;
        this.email = email;
        this.group = group;
        this.residence = residence;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGroup() {
        return group;
    }

    public String getResidence() {
        return residence;
    }

    public String getFunction() {
        return function;
    }
}
