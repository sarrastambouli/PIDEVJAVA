package GesUsers.tests;

import GesUsers.services.HuggingFaceService;

public class HuggingFaceTester {
    public static void main(String[] args) {
        HuggingFaceService service = new HuggingFaceService();

        try {
            System.out.println("Début de la transcription...");
            String result = service.transcribeAudio("C:\\Users\\nadaa\\OneDrive\\Téléchargements\\test.wav"); // Chemin relatif
            System.out.println("✅ Résultat : " + result);
        } catch (Exception e) {
            System.err.println("❌ Erreur : ");
            e.printStackTrace();
        }
    }
}