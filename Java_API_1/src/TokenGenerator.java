import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.*;

public class TokenGenerator{

	private String token;

	public void handle(String username, String password) throws IOException {
		// TODO Auto-generated method stub
			handleLogin(username, password);
	}

	private int getUserId(String username, String password) {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD)) {
			String query = "SELECT id FROM Users WHERE username=? AND password=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next() == true) {
						return Integer.valueOf(resultSet.getString(1));
					} // Returns true if the user is registered successfully.
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private void storeToken(int user_id, String token) {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD)) {
			String updateCheck = "SELECT * FROM Tokens WHERE user_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(updateCheck)) {
				preparedStatement.setInt(1, user_id);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next() != true) {
						String query = "INSERT INTO Tokens (user_id, token) VALUES (?, ?)";
						try (PreparedStatement preparedStatement1 = connection.prepareStatement(query)) {
							preparedStatement1.setInt(1, user_id);
							preparedStatement1.setString(2, token);
							int returnval = preparedStatement1.executeUpdate();
							if (returnval >= 1) {
								return; // Returns true if the token is stored successfully.
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						String query = "UPDATE Tokens SET token=? WHERE user_id=?";
						try (PreparedStatement preparedStatement1 = connection.prepareStatement(query)) {
							preparedStatement1.setString(1, token);
							preparedStatement1.setInt(2, user_id);
							int returnval = preparedStatement1.executeUpdate();
							if (returnval >= 1) {
								return; // Returns true if the token table is updated successfully.
							}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private String generateToken() {
		// TODO Auto-generated method stub
		// Generate a random token for simplicity (Later, can consider using a secure
		// token generation library)
		byte[] tokenBytes = new byte[16];
		new SecureRandom().nextBytes(tokenBytes);
		return bytesToHex(tokenBytes);
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
		for (byte b : bytes) {
			hexStringBuilder.append(String.format("%02x", b));
		}
		return hexStringBuilder.toString();
	}

	private void handleLogin(String username, String password) {
		// TODO Auto-generated method stub
			int user_id = getUserId(username, password);
			String token = generateToken();
			this.setToken(token);
			storeToken(user_id, token);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
