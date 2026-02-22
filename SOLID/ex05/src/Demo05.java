public class Demo05 {
    public static void main(String[] args) {
        System.out.println("=== Export Demo ===");

        ExportRequest req = new ExportRequest("Weekly Report", SampleData.longBody());
        Exporter pdf = new PdfExporter();
        Exporter csv = new CsvExporter();
        Exporter json = new JsonExporter();

        System.out.println("PDF: " + format(pdf.export(req)));
        System.out.println("CSV: " + format(csv.export(req)));
        System.out.println("JSON: " + format(json.export(req)));
    }

    private static String format(ExportResult r) {
        if (!r.success) {
            return "ERROR: " + r.errorMessage;
        }
        return "OK bytes=" + r.bytes.length;
    }
}
