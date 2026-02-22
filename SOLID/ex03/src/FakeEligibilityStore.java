public class FakeEligibilityStore implements EligibilityStore {
    public void save(String roll, String status) {
        System.out.println("Saved evaluation for roll=" + roll);
    }
}
