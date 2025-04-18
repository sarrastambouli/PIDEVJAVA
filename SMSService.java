package GesUsers.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {
    // Remplacez par vos identifiants Twilio
    public static final String ACCOUNT_SID = "àremplacer";
    public static final String AUTH_TOKEN = "à remplacer";
    public static final String TWILIO_NUMBER = "à remplacer"; // Votre numéro Twilio

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static String sendVerificationCode(String phoneNumber) {
        // Générer un code aléatoire (6 chiffres)
        String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));

        String messageBody = "Votre code de vérification EduCare: " + verificationCode;

        try {
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(TWILIO_NUMBER),
                    messageBody
            ).create();

            return verificationCode;
        } catch (Exception e) {
            System.err.println("Erreur d'envoi SMS: " + e.getMessage());
            return null;
        }
    }
}