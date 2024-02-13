import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class updateProfile {
	private String username;
	private String password;
	private String new_password;

	public String update(String username, String password, String new_password) throws SQLException {
		// TODO Auto-generated method stub
		usernameSetter(username);
		passwordSetter(password);
		String output = null;
		if (verifyUser() == true) {
			this.new_password = new_password;
			output = updateUserToDB();
		} else {
			output = "Wrong Credentials! Please Input Correct Username and Password.";
		}
		return output;
	}

	private boolean verifyUser() throws SQLException {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD)) {
			String query = "SELECT * FROM Users WHERE username=? AND password=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, this.username);
				preparedStatement.setString(2, this.password);
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private String updateUserToDB() {
		// TODO Auto-generated method stub
		try (Connection connection = DriverManager.getConnection(Application.DB_URL, Application.DB_USER,
				Application.DB_PASSWORD)) {
			String query = "UPDATE Users SET password=? WHERE username=?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, this.new_password);
				preparedStatement.setString(2, this.username);
				int returnval = preparedStatement.executeUpdate();
				if (returnval >= 1) {
					return "Password Updated"; // Returns true if the user is registered successfully.
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Registration Failed";
	}

	private void passwordSetter(String password) {
		// TODO Auto-generated method stub
		this.password = password;
	}

	private void usernameSetter(String username) {
		// TODO Auto-generated method stub
		this.username = username;
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
}
