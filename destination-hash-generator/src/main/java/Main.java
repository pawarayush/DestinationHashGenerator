// Main.java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        try {
            // Validate command line arguments
            if (args.length != 2) {
                System.err.println("Usage: java -jar test.jar <roll_number> <json_file_path>");
                System.exit(1);
            }

            String rollNumber = args[0];
            String jsonFilePath = args[1];

            // Read and parse JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(jsonFilePath));

            // Find first destination value
            String destinationValue = findDestination(rootNode);
            if (destinationValue == null) {
                System.err.println("No 'destination' key found in JSON file");
                System.exit(1);
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Generate MD5 hash
            String concatenated = rollNumber + destinationValue + randomString;
            String hash = generateMD5Hash(concatenated);

            // Output result in required format
            System.out.println(hash + ";" + randomString);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String findDestination(JsonNode node) {
        if (node.isObject()) {
            if (node.has("destination")) {
                return node.get("destination").asText();
            }
            for (JsonNode child : node) {
                String result = findDestination(child);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                String result = findDestination(element);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}