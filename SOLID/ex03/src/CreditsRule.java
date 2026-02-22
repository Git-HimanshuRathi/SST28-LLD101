public class CreditsRule implements EligibilityRule {
    @Override
    public String evaluate(StudentProfile student) {
        if (student.earnedCredits < 20) {
            return "credits below 20";
        }
        return null;
    }
}
