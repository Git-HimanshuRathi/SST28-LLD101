import java.util.*;

/**
 * SRP: Responsible only for parsing a raw key=value;... string into a Map.
 */
public class RawInputParser {

    public Map<String, String> parse(String raw) {
        Map<String, String> kv = new LinkedHashMap<>();
        String[] parts = raw.split(";");
        for (String p : parts) {
            String[] t = p.split("=", 2);
            if (t.length == 2)
                kv.put(t[0].trim(), t[1].trim());
        }
        return kv;
    }
}
