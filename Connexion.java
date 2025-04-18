package GesUsers.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {

    // 1. Définition des paramètres de connexion
    private static final String URL = "jdbc:mysql://localhost:3306/projpi";
    private static final String USER = "root";  // À modifier si nécessaire
    private static final String PASSWORD = "";  // Mot de passe MySQL, si existant

    // 2. Objet Connection (singleton)
    private static Connection conn;

    // 3. Constructeur privé pour éviter l'instanciation multiple
    private Connexion() {
    }

    // 4. Méthode pour récupérer la connexion
    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion réussie !");
            } catch (SQLException e) {
                System.out.println("❌ Erreur de connexion : " + e.getMessage());
            }
        }
        return conn;
    }
}
