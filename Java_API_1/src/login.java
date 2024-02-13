import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login {

	public String logger(String username, String password) {
		// TODO Auto-generated method stub
		return checkUser(username, password);
	}

	private String checkUser(String username, String password) {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD)) {
			String query = "SELECT * FROM Users WHERE username=? AND password=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if(resultSet.next()==true) {return "Login Successful"; }// Returns true if the user is registered successfully.
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Login Failed";
	}
}
