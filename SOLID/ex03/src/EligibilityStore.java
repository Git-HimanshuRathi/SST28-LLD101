/**
 * Abstraction for eligibility persistence.
 */
public interface EligibilityStore {
    void save(String roll, String status);
}
