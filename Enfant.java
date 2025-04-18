package GesUsers.entities;

public class Enfant {
    private int id;
    private int parentId;  // Clé étrangère vers User.id
    private String username;
    private String code;
    private String type;
    private int resQuiz;

    // Constructeur par défaut
    public Enfant() {}

    // Constructeur paramétré avec TOUS les attributs
    public Enfant(int id, int parentId, String username, String code, String type, int resQuiz) {
        this.id = id;
        this.parentId = parentId;
        this.username = username;
        this.code = code;
        this.type = type;
        this.resQuiz = resQuiz;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getResQuiz() { return resQuiz; }
    public void setResQuiz(int resQuiz) { this.resQuiz = resQuiz; }

}