/**
 * Result of a send operation. Replaces exceptions for failure reporting,
 * ensuring LSP compliance (no subtype throws for valid input).
 */
public class SendResult {
    public final boolean success;
    public final String errorMessage;

    private SendResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static SendResult ok() {
        return new SendResult(true, null);
    }

    public static SendResult error(String msg) {
        return new SendResult(false, msg);
    }
}
