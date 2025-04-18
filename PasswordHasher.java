package GesUsers.tools;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    // Coût du traitement (entre 4 et 31)
    private static final int WORKLOAD = 12;

    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plainPassword, salt);
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}