package GesUsers.tools;

import GesUsers.entities.Enfant;
import GesUsers.entities.User;

public class UserSession {
    private static UserSession instance;

    private User user;
    private Enfant enfant;
    private boolean isParentSession;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void startUserSession(User user) {
        this.user = user;
        this.enfant = null;
        this.isParentSession = true;
    }

    public void startEnfantSession(Enfant enfant) {
        this.enfant = enfant;
        this.user = null;
        this.isParentSession = false;
    }

    public void clearSession() {
        this.user = null;
        this.enfant = null;
    }

    // Getters
    public User getUser() {
        return user;
    }

    public Enfant getEnfant() {
        return enfant;
    }

    public boolean isParentSession() {
        return isParentSession;
    }

    public boolean isLoggedIn() {
        return user != null || enfant != null;
    }
}