import java.nio.charset.StandardCharsets;

/**
 * LSP-compliant: consistent null handling (null req handled by base class).
 */
public class JsonExporter extends Exporter {
    @Override
    protected ExportResult doExport(ExportRequest req) {
        String json = "{\"title\":\"" + escape(req.title) + "\",\"body\":\"" + escape(req.body) + "\"}";
        return new ExportResult("application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String s) {
        if (s == null)
            return "";
        return s.replace("\"", "\\\"");
    }
}
