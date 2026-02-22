import java.util.*;

/**
 * SRP-compliant orchestrator: delegates parsing, validation, persistence, and
 * printing
 * to dedicated components.
 */
public class OnboardingService {
    private final StudentRepository repository;
    private final RawInputParser parser;
    private final StudentValidator validator;
    private final OnboardingPrinter printer;

    public OnboardingService(StudentRepository repository) {
        this.repository = repository;
        this.parser = new RawInputParser();
        this.validator = new StudentValidator();
        this.printer = new OnboardingPrinter();
    }

    public void registerFromRawInput(String raw) {
        printer.printInput(raw);

        Map<String, String> kv = parser.parse(raw);

        List<String> errors = validator.validate(kv);
        if (!errors.isEmpty()) {
            printer.printErrors(errors);
            return;
        }

        String name = kv.getOrDefault("name", "");
        String email = kv.getOrDefault("email", "");
        String phone = kv.getOrDefault("phone", "");
        String program = kv.getOrDefault("program", "");

        String id = IdUtil.nextStudentId(repository.count());
        StudentRecord rec = new StudentRecord(id, name, email, phone, program);

        repository.save(rec);

        printer.printSuccess(id, repository.count(), rec);
    }
}
