import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        System.out.println("--- Generating Hashes ---");

        // 1. Define Users & Passwords
        String p1 = "adminpass";
        String p2 = "instpass";
        String p3 = "stupass1";
        String p4 = "stupass2";

        // 2. Generate Hashes
        String h1 = BCrypt.hashpw(p1, BCrypt.gensalt(12));
        String h2 = BCrypt.hashpw(p2, BCrypt.gensalt(12));
        String h3 = BCrypt.hashpw(p3, BCrypt.gensalt(12));
        String h4 = BCrypt.hashpw(p4, BCrypt.gensalt(12));

        // 3. Print for SQL
        System.out.println("admin (" + p1 + "): " + h1);
        System.out.println("inst1 (" + p2 + "): " + h2);
        System.out.println("stu1  (" + p3 + "): " + h3);
        System.out.println("stu2  (" + p4 + "): " + h4);
    }
}