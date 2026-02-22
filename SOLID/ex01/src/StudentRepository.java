import java.util.*;

/**
 * SRP/DIP: Abstraction for student persistence.
 */
public interface StudentRepository {
    void save(StudentRecord r);

    int count();

    List<StudentRecord> all();
}
