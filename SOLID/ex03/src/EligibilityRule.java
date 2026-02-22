/**
 * OCP: Each rule is a separate class implementing this interface.
 * New rules can be added without editing existing code.
 */
public interface EligibilityRule {
    /**
     * Evaluate the rule for the given student.
     * Returns null if the student passes, or a reason string if they fail.
     */
    String evaluate(StudentProfile student);
}
