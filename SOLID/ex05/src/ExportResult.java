public class ExportResult {
    public final String contentType;
    public final byte[] bytes;
    public final boolean success;
    public final String errorMessage;

    /** Successful result */
    public ExportResult(String contentType, byte[] bytes) {
        this.contentType = contentType;
        this.bytes = bytes;
        this.success = true;
        this.errorMessage = null;
    }

    /** Error result */
    public ExportResult(String errorMessage) {
        this.contentType = null;
        this.bytes = new byte[0];
        this.success = false;
        this.errorMessage = errorMessage;
    }
}
