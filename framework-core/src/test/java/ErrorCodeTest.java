import java.text.MessageFormat;

/**
 * @author: yibingzhou
 */
public class ErrorCodeTest {
    public static void main(String[] args) {
        String msg = "对象转换({0} to {1})失败:{2}";
        System.out.println(MessageFormat.format(msg, new Object[]{"a","b",2}));
    }
}
