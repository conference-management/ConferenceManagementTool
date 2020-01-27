package communication.utils;

import com.google.gson.Gson;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import communication.packettests.TestConnectionWrapper;
import communication.wrapper.Connection;
import org.junit.Assert;
import user.User;

import java.util.List;

public class MoreAsserts {

    /**
     * Asserts if two Lists are the same (without considering order)
     */
    public static void assertListEquals(List<Object> expected, List<Object> actual) {
        Assert.assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++) {
            for(int j = 0; j < actual.size(); j++) {
                if(expected.get(i).equals(actual.get(j))) {
                    expected.remove(i);
                    actual.remove(j);
                    i = -1;
                    break;
                }
            }
        }
        Assert.assertEquals(0, expected.size());
        Assert.assertEquals(0, actual.size());
    }

    public static void assertUserListEquals(List<User> expected, List<User> actual) {
        Assert.assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++) {
            for(int j = 0; j < actual.size(); j++) {
                if(Utils.equalsUser(expected.get(i), actual.get(j))) {
                    expected.remove(i);
                    actual.remove(j);
                    i = -1;
                    break;
                }
            }
        }
        Assert.assertEquals(0, expected.size());
        Assert.assertEquals(0, actual.size());
    }

    public static Connection assertValidResult() {
        return assertRequestResult(RequestResult.Valid);
    }

    /**
     * Returns a connection which asserts an result as soon as an answer has been received.
     *
     * @param expectedResult
     *
     * @return
     */
    public static Connection assertRequestResult(RequestResult expectedResult) {
        return new TestConnectionWrapper((answer) -> {
            ResponsePacket response = new Gson().fromJson(answer, ResponsePacket.class);
            Assert.assertSame(expectedResult, response.getResult());
        });
    }

    public static Connection assertFailureResult() {
        return assertRequestResult(RequestResult.Failure);
    }

    public static Connection assertInvalidToken() {
        return assertRequestResult(RequestResult.InvalidToken);
    }

    public static Connection assertClose() {
        return new TestConnectionWrapper((answer) -> {
            Assert.assertEquals("CLOSE", answer);
        });
    }
}
