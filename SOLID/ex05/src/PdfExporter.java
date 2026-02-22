import java.nio.charset.StandardCharsets;

/**
 * LSP-compliant: returns error result instead of throwing.
 */
public class PdfExporter extends Exporter {
    @Override
    protected ExportResult doExport(ExportRequest req) {
        String body = req.body == null ? "" : req.body;
        if (body.length() > 20) {
            return new ExportResult("PDF cannot handle content > 20 chars");
        }
        String fakePdf = "PDF(" + req.title + "):" + body;
        return new ExportResult("application/pdf", fakePdf.getBytes(StandardCharsets.UTF_8));
    }
}
