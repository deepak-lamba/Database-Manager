import java.io.*;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.*;

public class MyHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI s = exchange.getRequestURI();
		String path = s.getPath();

		Headers headerData = exchange.getRequestHeaders();
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
		}
		String token;
		String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
		if (cookieHeader != null) {
			HttpCookie cookie = HttpCookie.parse(cookieHeader).get(0);
            token = cookie.getValue();
			if (!verifyToken(token)) {
				sendResponse(exchange, 401, "Not Authorized"); return;
			}
		} else {
			sendResponse(exchange, 401, "Not Authorized"); return;
		}

		if (("/logs").equals(path)) {
			handleLogs(exchange);
		} else if (("/messages").equals(path)) {
			try {
				handleMessages(exchange);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (("/my/messages").equals(path)) {
			try {
				handleMyMessages(exchange);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (("/update_profile").equals(path)) {
			try {
				handleUpdateProfile(exchange);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (("/operations").equals(path)) {
			try {
				handleOperations(exchange);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (("/request").equals(path)) {
			try {
				handleQueryRequest(exchange);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (("/update/messages").equals(path)) {
			try {
				handleUpdateMessageRequest(exchange);
			} catch (IOException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return;
	}

	private void handleUpdateMessageRequest(HttpExchange exchange) throws IOException, ParseException {
		// TODO Auto-generated method stub
		String requestBody = readRequestBody(exchange);
		String response = "Request Failed";
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(requestBody);

		int id = 0;
		String comments = null;
		String status = null;
		Timestamp time_stamp = null;
		
		try {
			id = Integer.valueOf(json.get("id").toString());
			comments = json.get("comments").toString();
			status = json.get("status").toString();
			time_stamp = new Timestamp(System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
			response = "Data missing in the query: " + requestBody;
			sendResponse(exchange, 200, response);
		}
		
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {

			String query = "UPDATE Logs SET comments=?, status=?, time_stamp=? WHERE id=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setInt(4, id);
				preparedStatement.setString(1, comments);
				preparedStatement.setString(2, status);
				preparedStatement.setTimestamp(3, time_stamp);
				int returnval = preparedStatement.executeUpdate();
				if (returnval >= 1) {
					response = "Logs Updated"; // Returns true if the Updated Log is registered successfully.
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response = "Error executing the query: " + e.getMessage();
		}
		sendResponse(exchange, 200, response);
	}

	private void handleMyMessages(HttpExchange exchange) throws IOException, ParseException {
		// TODO Auto-generated method stub
		int requester_user_id = getUserId(exchange);
		String response = sendMyMessagesLogged(requester_user_id);
		sendResponse(exchange, 200, response);
	}

	private String sendMyMessagesLogged(int requester_user_id) {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {
			String sqlQuery = "SELECT * FROM Logs WHERE requester_user_id=?";
			// Execute the SQL query
			try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery)) {
				preparedStatement1.setInt(1, requester_user_id);
				ResultSet resultSet = preparedStatement1.executeQuery();
				return resultSetToJson(resultSet); // Returns true if the Query Request is stored successfully.
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error fetching the Logs";
		}
	}

	private void handleQueryRequest(HttpExchange exchange)
			throws IOException, ParseException, java.text.ParseException {
		// TODO Auto-generated method stub
		String requestBody = readRequestBody(exchange);
		String response = parseQueryRequest(requestBody);

		sendResponse(exchange, 200, response);
	}

	private String parseQueryRequest(String requestBody) throws ParseException, java.text.ParseException {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(requestBody);

		int requester_user_id = 0;
		String requester_name = null;
		String query = null;
		String comments = null;
		int approver_user_id = 0;
		String approver_name = null;
		String status = null;
		Timestamp time_stamp = null;

		try {
			requester_user_id = Integer.valueOf(json.get("requester_user_id").toString());
			requester_name = json.get("requester_name").toString();
			query = json.get("query").toString();
			comments = json.get("comments").toString();
			approver_user_id = Integer.valueOf(json.get("approver_user_id").toString());
			approver_name = json.get("approver_name").toString();
			status = json.get("status").toString();
			time_stamp = new Timestamp(System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
			return "Data missing in the query: " + requestBody;
		}
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {

			String sqlQuery = "INSERT INTO Logs (requester_user_id, requester_name, query, comments, approver_user_id, approver_name, status, time_stamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			// For other queries (INSERT, UPDATE, DELETE), return the row count
			try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery)) {
				preparedStatement1.setInt(1, requester_user_id);
				preparedStatement1.setString(2, requester_name);
				preparedStatement1.setString(3, query);
				preparedStatement1.setString(4, comments);
				preparedStatement1.setInt(5, approver_user_id);
				preparedStatement1.setString(6, approver_name);
				preparedStatement1.setString(7, status);
				preparedStatement1.setTimestamp(8, time_stamp);
				int returnval = preparedStatement1.executeUpdate();
				if (returnval >= 1) {
					return "Request Sent"; // Returns true if the Query Request is stored successfully.
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error executing the query: " + e.getMessage();
		}
		return "Request Failed";
	}

	private void handleOperations(HttpExchange exchange) throws IOException, ParseException {
		// TODO Auto-generated method stub
		// Read the request body to get CRUD operation details (in JSON format, for
		// example)
		String requestBody = readRequestBody(exchange);

		// Parse the request body to get operation details
		String response = parseOperationType(requestBody);

		// Send the response
		sendResponse(exchange, 200, response);
	}

	private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
		// TODO Auto-generated method stub
		exchange.sendResponseHeaders(statusCode, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes(StandardCharsets.UTF_8));
		os.close();
	}

	private String parseOperationType(String requestBody) throws ParseException {
		// TODO Auto-generated method stub
		// Execute the SQL query
		String queryExtract = extractSqlQuery(requestBody);
		String queryResult = executeSqlQuery(queryExtract);

		// Send the query result as the response
		return queryResult;
	}

	private String extractSqlQuery(String requestBody) throws ParseException {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(requestBody);

		String query = json.get("query").toString();
		return query;
	}

	private String executeSqlQuery(String sqlQuery) {
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {

			// Execute the SQL query
			if (!sqlQuery.trim().toUpperCase().contains("USERS") && !sqlQuery.trim().toUpperCase().contains("TOKENS")
					&& !sqlQuery.trim().toUpperCase().contains("LOGS")) {
				if (sqlQuery.trim().toUpperCase().startsWith("SELECT")) {
					// For SELECT queries, return the result set as a JSON string (for simplicity)
					ResultSet resultSet = statement.executeQuery(sqlQuery);
					return resultSetToJson(resultSet);
				} else {
					// For other queries (INSERT, UPDATE, DELETE), return the row count
					int rowCount = statement.executeUpdate(sqlQuery);
					return "Query executed successfully. Rows affected: " + rowCount;
				}
			} else {
				return " Restricted Data ! Access Denied. ";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "Error executing the query: " + e.getMessage();
		}
	}

	private String resultSetToJson(ResultSet resultSet) throws SQLException {
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

	private String readRequestBody(HttpExchange exchange) throws IOException {
		InputStream requestBody = exchange.getRequestBody();
		BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody, StandardCharsets.UTF_8));
		StringBuilder requestBodyText = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBodyText.append(line);
		}
		return requestBodyText.toString();
	}

	private void handleUpdateProfile(HttpExchange exchange) throws ParseException, IOException, SQLException {
		// TODO Auto-generated method stub
		String requestBody = readRequestBody(exchange);
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(requestBody);

		String username = json.get("username").toString();
		String password = json.get("password").toString();
		String new_password = json.get("new_password").toString();

		String val = new updateProfile().update(username, password, new_password);
		if (val.equals("Password Updated")) {
			TokenGenerator TK=new TokenGenerator();
			TK.handle(username, new_password);
			exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + TK.getToken() + "; HttpOnly" + "; SameSite=None; Secure");
		}
		sendResponse(exchange, 200, val);
	}

	private void handleMessages(HttpExchange exchange) throws IOException, ParseException {
		// TODO Auto-generated method stub
		int approver_user_id = getUserId(exchange);
		String response = sendMessagesLogged(approver_user_id);

		sendResponse(exchange, 200, response);
	}

	private int getUserId(HttpExchange exchange) throws ParseException {
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

	private String sendMessagesLogged(int approver_user_id) {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {
			String sqlQuery = "SELECT * FROM Logs WHERE approver_user_id=?";
			// Execute the SQL query
			try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery)) {
				preparedStatement1.setInt(1, approver_user_id);
				ResultSet resultSet = preparedStatement1.executeQuery();
				return resultSetToJson(resultSet); // Returns true if the Query Request is stored successfully.
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error fetching the Logs";
		}
	}

	private void handleLogs(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		String response = sendAllLogs();

		sendResponse(exchange, 200, response);
	}

	private String sendAllLogs() {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD); Statement statement = connection.createStatement()) {
			String sqlQuery = "SELECT * FROM Logs";
			// Execute the SQL query
			ResultSet resultSet = statement.executeQuery(sqlQuery);
			return resultSetToJson(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error fetching the Logs";
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
}
