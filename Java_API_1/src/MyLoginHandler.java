import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;

public class MyLoginHandler implements HttpHandler {

	private String token;

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI s = exchange.getRequestURI();
		String path = s.getPath();

		Headers headerData = exchange.getRequestHeaders();

		// Inside the handle method of your HttpHandler
		Headers headers = exchange.getResponseHeaders();
		headers.add("Access-Control-Allow-Origin", "http://localhost:4200"); // Allow requests from any origin
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Allow specific HTTP methods
		headers.add("Access-Control-Allow-Headers", "*"); // Allow specific request headers
		headers.add("Access-Control-Allow-Credentials", "true");

		if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
			// Handle CORS preflight requests
			exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
			exchange.sendResponseHeaders(204, -1); // No content
		} else if (("/").equals(path) && exchange.getRequestMethod().equals("POST")) {
			try {
				handleLogin(exchange);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (("/users").equals(path) && exchange.getRequestMethod().equals("GET")) {
			
			String token;
			String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
			if (cookieHeader != null) {
				HttpCookie cookie = HttpCookie.parse(cookieHeader).get(0);
	            token = cookie.getValue();
				if (!verifyToken(token)) {
					sendResponse(exchange, 401, "Not Authorized");
				}
			} else {
				sendResponse(exchange, 401, "Not Authorized");
			}
		
			handleUser(exchange);
		}
	}

	public static boolean verifyToken(String token) {
		// SQL query to check if the token exists in the Tokens table
		String query = "SELECT COUNT(*) FROM Tokens WHERE token = ?";

		try (
				// Establish database connection
				Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
						Application.DB_PASSWORD);
				// Prepare SQL statement
				PreparedStatement statement = connection.prepareStatement(query)) {
			// Set the token parameter in the SQL query
			statement.setString(1, token);

			// Execute the query
			try (ResultSet resultSet = statement.executeQuery()) {
				// Check if any rows are returned (token exists)
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					return count > 0; // Return true if token exists, false otherwise
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); // Handle database connection or query errors
		}
		return false; // Return false if an error occurred or token doesn't exist
	}

	private void handleUser(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		int user_id = getUserId(exchange);
		String name = getUserName(user_id);
		String list = sendUsersList();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user_id", user_id);
        jsonObject.put("user_name", name);
        jsonObject.put("userList", list);
		String response = jsonObject.toString();
         sendResponse(exchange, 200, response);
	}

	private String getUserName(int user_id) {
		// TODO Auto-generated method stub
		if (user_id != -1) {

			try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
					Application.DB_PASSWORD); Statement statement = connection.createStatement()) {
				String sqlQuery = "SELECT name FROM Users WHERE id=?";
				// Execute the SQL query
				try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery)) {
					preparedStatement1.setInt(1, user_id);
					try (ResultSet resultSet = preparedStatement1.executeQuery()) {
						resultSet.next();
						String value = resultSet.getString("name"); // Returns true if the user is registered
						return value;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return "Error in Fetching User Data";
			}
		}
		return "User Not Found";
	}

	private int getUserId(HttpExchange exchange) {
		// TODO Auto-generated method stub
		List<String> AuthHeader = exchange.getRequestHeaders().get("Authorization");
		String token = null;
		if (AuthHeader != null) {
			token = AuthHeader.get(0);

			try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
					Application.DB_PASSWORD); Statement statement = connection.createStatement()) {
				String sqlQuery = "SELECT user_id FROM Tokens WHERE token=?";
				// Execute the SQL query
				try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery)) {
					preparedStatement1.setString(1, token);
					try (ResultSet resultSet = preparedStatement1.executeQuery()) {
						resultSet.next();
						int value = resultSet.getInt("user_id"); // Returns true if the user is registered
						return value;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return -1;
	}

	private String sendUsersList() {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {
			String sqlQuery = "SELECT id, name FROM Users";
			// Execute the SQL query
			try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery)) {
				ResultSet resultSet = preparedStatement1.executeQuery();
				return resultSetToJson(resultSet); // Returns true if the Query Request is stored successfully.
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error fetching the Users List";
		}
	}

	private String resultSetToJson(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();

		while (resultSet.next()) {
			JSONObject jsonRow = new JSONObject();

			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				String columnName = resultSet.getMetaData().getColumnName(i);
				String columnValue = resultSet.getString(i);

				jsonRow.put(columnName, columnValue);
			}

			jsonArray.add(jsonRow);
		}
		return jsonArray.toString();

	}

	private void handleLogin(HttpExchange exchange) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
		StringBuilder requestBodyText = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBodyText.append(line);
		}
		String requestBodyTextTrimmed = requestBodyText.toString();
		// Further processing...
		String username = extractValue(requestBodyTextTrimmed, "username");
		String password = extractValue(requestBodyTextTrimmed, "password");
		String val = new login().logger(username, password);
		if (val.equals("Login Successful")) {
			TokenGenerator TK = new TokenGenerator();
			TK.handle(username, password);
			exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + TK.getToken() + "; HttpOnly" + "; SameSite=None; Secure");
		}
		sendResponse(exchange, 200, val);
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

	private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
		// TODO Auto-generated method stub
		exchange.sendResponseHeaders(statusCode, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes(StandardCharsets.UTF_8));
		os.close();
	}
}
