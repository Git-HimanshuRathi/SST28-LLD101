public class AttendanceRule implements EligibilityRule {
    @Override
    public String evaluate(StudentProfile student) {
        if (student.attendancePct < 75) {
            return "attendance below 75";
        }
        return null;
    }
}
