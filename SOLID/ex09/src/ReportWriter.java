public class ReportWriter implements Writer {
    @Override
    public String write(Submission s, int plag, int code) {
        return "report-" + s.roll + ".txt";
    }
}
