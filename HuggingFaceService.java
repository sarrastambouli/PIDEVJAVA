package GesUsers.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HuggingFaceService {
    private static final String HF_API_KEY = "à remplacer"; // Remplacez par votre vrai token
    private static final String HF_API_URL = "à remplacer";

    public String transcribeAudio(String audioPath) throws Exception {
        // 1. Convertir l'audio en base64
        byte[] audioBytes = Files.readAllBytes(Paths.get(audioPath));
        String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

        // 2. Préparer la requête HTTP
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(HF_API_URL);
        post.setHeader("Authorization", "Bearer " + HF_API_KEY);
        post.setHeader("Content-Type", "application/json");

        // 3. Construire le corps JSON avec GSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputs", audioBase64);
        post.setEntity(new StringEntity(requestBody.toString()));

        // 4. Exécuter la requête
        String response = EntityUtils.toString(client.execute(post).getEntity());

        // 5. Parser la réponse avec GSON
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

        // Gestion des erreurs de l'API
        if (jsonResponse.has("error")) {
            throw new Exception("Erreur Hugging Face: " + jsonResponse.get("error").getAsString());
        }

        return jsonResponse.get("text").getAsString(); // Retourne le texte transcrit
    }
}