import java.sql.*;
import java.sql.SQLException;

public class registration {
	private String username;
	private String name;
	private String password;
	private String role;

	public String register(String username, String name, String password, String role) {
		// TODO Auto-generated method stub
		usernameSetter(username);
		nameSetter(name);
		passwordSetter(password);
		roleSetter(role);
		String output = saveUserToDB();
		return output;
	}

	private String saveUserToDB() {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD)) {
			String query = "INSERT INTO Users (username, name, password, role) VALUES ( ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, this.username);
				preparedStatement.setString(2, this.name);
				preparedStatement.setString(3, this.password);
				preparedStatement.setString(4, this.role);
				int returnval = preparedStatement.executeUpdate();
				if (returnval >= 1) {
					return "Registration Successful"; // Returns true if the user is registered successfully.
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Registration Failed";
	}

	private void roleSetter(String role) {
		// TODO Auto-generated method stub
		this.role = role;
	}

	private void passwordSetter(String password) {
		// TODO Auto-generated method stub
		this.password = password;
	}

	private void nameSetter(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	private void usernameSetter(String username) {
		// TODO Auto-generated method stub
		this.username = username;
	}

}
