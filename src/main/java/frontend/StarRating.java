package frontend;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StarRating extends HBox {
    private int rating = 0;
    private final Button[] stars;
    private final int starSize;

    public interface RatingChangeListener {
        void onRatingChanged(int stars);
    }

    private RatingChangeListener listener;

    public StarRating(int numberOfStars, int starSize) {
        this.starSize = starSize;
        this.stars = new Button[numberOfStars];

        for (int i = 0; i < stars.length; i++) {
            final int starValue = i + 1;
            stars[i] = new Button("☆");
            stars[i].setStyle(String.format("-fx-background-color: transparent; -fx-font-size: %d;", starSize));
            stars[i].setTextFill(Color.GOLD);

            stars[i].setOnMouseEntered(e -> highlightStars(starValue));
            stars[i].setOnMouseExited(e -> highlightStars(rating));
            stars[i].setOnAction(e -> setRating(starValue));

            this.getChildren().add(stars[i]);
        }
    }

    private void highlightStars(int upTo) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText(i < upTo ? "★" : "☆");
        }
    }

    public void setRating(int rating) {
        this.rating = rating;
        highlightStars(rating);
        if (listener != null) {
            listener.onRatingChanged(rating);
        }
    }

    public int getRating() {
        return rating;
    }

    public void setOnRatingChanged(RatingChangeListener listener) {
        this.listener = listener;
    }
}