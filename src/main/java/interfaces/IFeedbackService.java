package interfaces;

import entities.Feedback;

import java.util.List;

public interface IFeedbackService {
    void addFeedback(Feedback feedback);
    List<Feedback> getAllFeedbacks();
    Feedback getFeedbackById(int id);
    void supprimerFeedback(int id);
    void modifierFeedback(Feedback feedback);
}
