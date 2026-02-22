/**
 * LSP-compliant base contract:
 * 1. export() never throws for non-null ExportRequest
 * 2. export() always returns a non-null ExportResult
 * 3. If format-specific constraints are violated, return an error ExportResult
 * 4. All exporters handle null body gracefully (treat as empty string)
 */
public abstract class Exporter {

    public final ExportResult export(ExportRequest req) {
        if (req == null) {
            return new ExportResult("request must not be null");
        }
        return doExport(req);
    }

    /**
     * Subclasses implement format-specific export logic.
     * They may assume req is non-null.
     */
    protected abstract ExportResult doExport(ExportRequest req);
}
