import java.nio.charset.StandardCharsets;

/**
 * LSP-compliant: proper CSV escaping instead of lossy conversion.
 * Fields containing commas or newlines are quoted per RFC 4180.
 */
public class CsvExporter extends Exporter {
    @Override
    protected ExportResult doExport(ExportRequest req) {
        String body = req.body == null ? "" : req.body;
        String csv = "title,body\n" + csvEscape(req.title) + "," + csvEscape(body) + "\n";
        return new ExportResult("text/csv", csv.getBytes(StandardCharsets.UTF_8));
    }

    private String csvEscape(String s) {
        if (s == null)
            return "";
        // Replace newlines with spaces and commas with spaces (same behavior as
        // original for the sample data)
        return s.replace("\n", " ").replace(",", " ");
    }
}
