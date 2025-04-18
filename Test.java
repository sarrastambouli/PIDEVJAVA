package GesUsers.tests;

import GesUsers.tools.Connexion;
import java.sql.Connection;
import GesUsers.entities.User;
import GesUsers.entities.Enfant;
import GesUsers.services.UserService;
import GesUsers.services.EnfantService;
import java.sql.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Test {

    public static void main(String[] args) {
        // On teste la connexion
        Connection conn = Connexion.getConnection();
        if (conn != null) {
            System.out.println("🎉 Connexion établie avec succès !");
        } else {
            System.out.println("❌ Échec de la connexion.");
        }

        UserService userService = new UserService();

        // Test d'ajout
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse("1985-05-15");
            Date sqlDate = new Date(utilDate.getTime());

            User user = new User("Doe", "John", "john@example.com", "mdp123", "parent", "enfant_unique", "0612345678", sqlDate, 85);
            userService.addUser(user);
            System.out.println("✅ User ajouté avec ID: " + user.getId());
        } catch (ParseException e) {
            System.err.println("Erreur de format de date lors de l'ajout");
        }

        // Test de getAllUsers()
        List<User> users = userService.getAllUsers();
        System.out.println("\nListe des utilisateurs :");
        for (User u : users) {
            System.out.println(u.getId() + " | " + u.getNom() + " " + u.getPrenom() + " | " + u.getEmail() + " | " + u.getRole() + " | " + u.getTypeEnfant() + " | " + u.getNumTel() + " | " + u.getDateNaissance() + " | " + u.getResultatQuiz());
        }

        // Suppression de l'utilisateur avec ID 20
        userService.deleteUser(20);

        // Récupération d'un utilisateur par ID
        User u = userService.getUserById(30);
        if (u != null) {
            System.out.println(u.getId() + " | " + u.getNom() + " " + u.getPrenom() + " | " + u.getEmail() + " | " + u.getRole() + " | " + u.getTypeEnfant() + " | " + u.getNumTel() + " | " + u.getDateNaissance() + " | " + u.getResultatQuiz());
        }

        // Mise à jour d'un utilisateur
        User modifications = new User();
        modifications.setId(30); // Même ID que celui récupéré
        modifications.setNom("NouveauNom");
        modifications.setPrenom("NouveauPrénom");
        modifications.setEmail("nouveau@email.com");
        modifications.setRole("nouveauRole");
        modifications.setTypeEnfant("nouveauTypeEnfant");
        modifications.setNumTel("nouveauNumTel");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse("2000-01-01");
            Date sqlDate = new Date(utilDate.getTime());
            modifications.setDateNaissance(sqlDate);
        } catch (ParseException e) {
            System.err.println("Format de date invalide, mise à null");
            modifications.setDateNaissance(null);
        }

        System.out.println("\n=== TENTATIVE DE MODIFICATION ===");
        boolean succes = userService.updateUser(modifications);

        if (succes) {
            System.out.println("✅ Modification réussie");
            System.out.println("\n=== APRÈS MODIFICATION ===");
            User userApres = userService.getUserById(30);
            if (userApres != null) {
                System.out.println("Nom: " + userApres.getNom());
                System.out.println("Prénom: " + userApres.getPrenom());
                System.out.println("Email: " + userApres.getEmail());
                System.out.println("Role: " + userApres.getRole());
                System.out.println("TypeEnfant: " + userApres.getTypeEnfant());
                System.out.println("NumTel: " + userApres.getNumTel());
                System.out.println("DateNaissance: " + userApres.getDateNaissance());
                System.out.println("ResultatQuiz: " + userApres.getResultatQuiz());
            }
        } else {
            System.out.println("❌ Échec de la modification");
        }

        // Test du CRUD enfant
        EnfantService enfantService = new EnfantService();

        // Test ajout
        Enfant nouvelEnfant = new Enfant();
        nouvelEnfant.setParentId(22); // ID d'un parent existant
        nouvelEnfant.setUsername("enfanttt");
        nouvelEnfant.setCode("ABC123");
        nouvelEnfant.setType("normal");
        enfantService.addEnfant(nouvelEnfant);

        // Test suppression
        enfantService.deleteEnfant(nouvelEnfant.getId());

        // Test récupération
        List<Enfant> enfants = enfantService.getEnfantsByParent(22);
        if (enfants != null) {
            enfants.forEach(e -> System.out.println(e.getUsername()));
        }
    }
}