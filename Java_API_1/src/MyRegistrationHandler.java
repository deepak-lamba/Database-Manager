import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import com.sun.net.httpserver.*;

public class MyRegistrationHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI s = exchange.getRequestURI();
		String path = s.getPath();

		// Inside the handle method of your HttpHandler
		Headers headers = exchange.getResponseHeaders();
		headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Allow specific HTTP methods
		headers.add("Access-Control-Allow-Headers", "*"); // Allow specific request headers
		headers.add("Access-Control-Allow-Credentials", "true");

		if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
			// Handle CORS preflight requests
			exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
			exchange.sendResponseHeaders(204, -1); // No content
		} else if (("/registration").equals(path) && exchange.getRequestMethod().equals("POST")) {
			String response = null;
			try {
				response = handleRegistration(exchange);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes(StandardCharsets.UTF_8));
			os.close();
		}
	}

	private String handleRegistration(HttpExchange exchange) throws IOException, ParseException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBodyText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBodyText.append(line);
        }
        String requestBodyTextTrimmed = requestBodyText.toString();

        String name = extractValue(requestBodyTextTrimmed, "name");
        String username = extractValue(requestBodyTextTrimmed, "username");
        String password = extractValue(requestBodyTextTrimmed, "password");
        String role = extractValue(requestBodyTextTrimmed, "role");

		return new registration().register(username, name, password, role);
	}
	
	private static String extractValue(String jsonString, String input) {
        // Find the index of the colon (:) after the field name
		int Index = jsonString.indexOf(input);
        int colonIndex = jsonString.indexOf(":", Index);

        // Find the index of the opening double quote (") after the colon
        int quoteIndex = jsonString.indexOf('"', colonIndex);

        // Find the index of the closing double quote (") after the opening double quote
        int closingQuoteIndex = jsonString.indexOf('"', quoteIndex + 1);

        // Extract the value between the opening and closing double quotes
        return jsonString.substring(quoteIndex + 1, closingQuoteIndex);
    }

}
