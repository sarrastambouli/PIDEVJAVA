package tools;
import java.sql.Connection; // elle appelle les méthodes statiques de la connexion à la bd //
import java.sql.DriverManager; // elle appelle le JDBC eli nty sabitou //
import java.sql.SQLException; // elle va gérer les problèmes de bd //
public class MyConnection {

    private String url="jdbc:mysql://localhost:3306/educare";
    private String login="root";
    private String pwd="AZEQSDWXC123.";

    private Connection cnx;
    private static MyConnection instance;

    private MyConnection(){
        try {
            cnx =  DriverManager.getConnection(url,login,pwd);
            System.out.println("Connexion established and opened...");
        } catch (SQLException a) {
            System.out.println(a.getMessage());
        }
    } // yesnaa instance de connexion

    public Connection getCnx() {
        return cnx;
    } // yrajaahelek

    public static MyConnection getInstance(){
        if(instance == null){
            instance = new MyConnection();
        }
        return instance;
    }
    // bch ken nty deja connexion mahloula matawwedch thelha mafamrch surcharge aal ram
}

