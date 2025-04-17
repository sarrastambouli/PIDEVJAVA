package tests;


import tools.MyConnection;
import entities.Quiz;
import services.QuizService;
import java.util.List;


public class test {

    public static void main(String[] args) {
        // Obtenir l'instance de MyConnection et tester la connexion
        MyConnection connection = MyConnection.getInstance();
        if (connection.getCnx() != null) {
            System.out.println("La connexion est établie avec succès !");
        } else {
            System.out.println("Erreur de connexion.");
        }

    QuizService quizService = new QuizService();

    // 1. Test de l'ajout d'un quiz
    Quiz quiz = new Quiz();
        quiz.setNomQuiz("quiz1");
        quiz.setDescriptionQuiz("cest un quiz de premiere connexion");
        quiz.setNombreQuestions(2);
        quizService.addQuiz(quiz);

        Quiz quiz1 = new Quiz();
        quiz.setNomQuiz("quiz2");
        quiz.setDescriptionQuiz("cest un quiz 2 de premiere connexion");
        quiz.setNombreQuestions(3);
        quizService.addQuiz(quiz);

        Quiz quiz2 = new Quiz();
        quiz.setNomQuiz("quiz3");
        quiz.setDescriptionQuiz("cest un quiz 3 de premiere connexion");
        quiz.setNombreQuestions(8);
        quizService.addQuiz(quiz);

        System.out.println("______________________________________________");


        // 2. Test de récupération de tous les quizzes
   List<Quiz> quizzes = quizService.getAllQuizzes();
     System.out.println("Liste des quizzes :");
      for (Quiz q : quizzes) {
      System.out.println("Quiz ID: " + q.getId() + ", Nom: " + q.getNomQuiz() + ", Description: " + q.getDescriptionQuiz() + ", Nombre de questions: " + q.getNombreQuestions());
   }
        System.out.println("______________________________________________");

    // 3. Test de récupération d'un quiz par ID
    int quizId = quizzes.get(0).getId();  // Utiliser l'ID du premier quiz
    Quiz quizFromDb = quizService.getQuizById(quizId);
    System.out.println("Quiz récupéré par ID: " + quizFromDb.getNomQuiz());

        System.out.println("______________________________________________");

        // 4. Test de la mise à jour du quiz
      quiz.setNomQuiz("quiz3testUPDATE");
       quiz.setNombreQuestions(5);
        quiz.setDescriptionQuiz("cest un test de modification de quiz ");
        quizService.updateQuiz(quiz, 6);

        System.out.println("______________________________________________");

     // 4.1. Test de récupération de tous les quizzes après modification
       List<Quiz> quizzesUPDATED = quizService.getAllQuizzes();
        System.out.println("Liste des quizzes après mise à jour:");
        for (Quiz q : quizzesUPDATED) {
           System.out.println("Quiz ID: " + q.getId() + ", Nom: " + q.getNomQuiz() + ", Description: " + q.getDescriptionQuiz() + ", Nombre de questions: " + q.getNombreQuestions());
        }

      System.out.println("______________________________________________");

        // 5. Test de suppression du quiz
       // quizService.deleteQuiz(3);
       // System.out.println("Quiz supprimé avec succès.");


     //   System.out.println("______________________________________________");


    // Vérification de la suppression en récupérant à nouveau la liste des quizzes
  // quizzes = quizService.getAllQuizzes();
 // System.out.println("Liste des quizzes après suppression :");
  //for (Quiz q : quizzes) {
   // System.out.println("Quiz ID: " + q.getId() + ", Nom: " + q.getNomQuiz());
 // }

   System.out.println("______________________________________________");

    }
}

