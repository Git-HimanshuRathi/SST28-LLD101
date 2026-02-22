import java.util.*;

/**
 * OCP-compliant: iterates over a list of rules. New rules are added by
 * creating a new class and adding it to the list — no edits to this engine.
 *
 * Note: the original code used if/else-if which short-circuits after the first
 * failure.
 * We preserve the same short-circuit behavior to match the original output.
 */
public class EligibilityEngine {
    private final List<EligibilityRule> rules;
    private final EligibilityStore store;

    public EligibilityEngine(List<EligibilityRule> rules, EligibilityStore store) {
        this.rules = rules;
        this.store = store;
    }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityResult r = evaluate(s);
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityResult evaluate(StudentProfile s) {
        List<String> reasons = new ArrayList<>();
        String status = "ELIGIBLE";

        for (EligibilityRule rule : rules) {
            String reason = rule.evaluate(s);
            if (reason != null) {
                status = "NOT_ELIGIBLE";
                reasons.add(reason);
                break; // preserve short-circuit behavior from original if/else-if
            }
        }

        return new EligibilityResult(status, reasons);
    }
}
