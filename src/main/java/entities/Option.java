package entities;

public class Option {

    private int id;
    private int questionId;
    private String contenuOption;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private boolean estCorrecte;

    public Option(int id, int questionId, String contenuOption, String image1, String image2, String image3, String image4, boolean estCorrecte) {
        this.id = id;
        this.questionId = questionId;
        this.contenuOption = contenuOption;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.estCorrecte = estCorrecte;
    }

    public Option() {

    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getContenuOption() { return contenuOption; }
    public void setContenuOption(String contenuOption) { this.contenuOption = contenuOption; }

    public String getImage1() { return image1; }
    public void setImage1(String image1) { this.image1 = image1; }

    public String getImage2() { return image2; }
    public void setImage2(String image2) { this.image2 = image2; }

    public String getImage3() { return image3; }
    public void setImage3(String image3) { this.image3 = image3; }

    public String getImage4() { return image4; }
    public void setImage4(String image4) { this.image4 = image4; }

    public boolean isEstCorrecte() { return estCorrecte; }
    public void setEstCorrecte(boolean estCorrecte) { this.estCorrecte = estCorrecte; }
}
