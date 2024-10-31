import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class testGPT {
    private static final String API_KEY = "heV6jx5Je65dltuYHdlXPnTD";
    private static final String SECRET_KEY = "zJSq1KO3gQ8nGStocCYFzLAYSYPKY3iX";
    private static final String FILE_PATH = "C:/Users/wild1/Desktop/test/";
    private static final String INPUT_FILE = "questions.txt";
    private static final String OUTPUT_FILE = "answers.txt";


    public static void main(String[] args) {
        try {
            String question = readQuestion(FILE_PATH + INPUT_FILE);
            System.out.println(question);
            String answer = askQuestion(question);
            writeAnswer(FILE_PATH + OUTPUT_FILE, answer);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static String readQuestion(String filePath) throws IOException {
        StringBuilder questionBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                questionBuilder.append(line).append(" ");
            }
        }
        return questionBuilder.toString().trim();
    }

    private static String askQuestion(String question) throws IOException, ParseException {
        String accessToken = getAccessToken();
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + accessToken;

        String payload = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + question + "\"}]}";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response);
            return parseResult(response.toString());
        }
    }

    private static String getAccessToken() throws IOException, ParseException {
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return parseToken(response.toString());
        }
    }

    private static String parseToken(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(json);
        return (String) jsonObject.get("access_token");
    }

    private static String parseResult(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(json);
        return (String) jsonObject.get("result");
    }

    private static void writeAnswer(String filePath, String answer) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(answer);
        }
    }
}
