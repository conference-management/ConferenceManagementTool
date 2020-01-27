package communication.utils;

import user.User;

public class Utils {


    /**
     * Compares two expected equals another one just regarding their data.
     *
     * @param expected expected to compare to
     * @param actual   the actual user
     *
     * @return true iff the expected and actual user have the same data
     */
    public static boolean equalsUser(User expected, User actual) {
        boolean result = true;
        result = result && expected.getEmail().equals(actual.getEmail());
        result = result && expected.getFunction().equals(actual.getFunction());
        result = result && expected.getGroup().equals(actual.getGroup());
        result = result && expected.getName().equals(actual.getName());
        result = result && expected.getResidence().equals(actual.getResidence());
        return result;
    }
}
