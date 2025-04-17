package tests;


import tools.MyConnection;

public class test {

    public static void main(String[] args) {
        // Obtenir l'instance de MyConnection et tester la connexion
        MyConnection connection = MyConnection.getInstance();
        if (connection.getCnx() != null) {
            System.out.println("La connexion est établie avec succès !");
        } else {
            System.out.println("Erreur de connexion.");
        }
    }
}
