package pjwstk.tpo_6.mingle.Repositories;

import pjwstk.tpo_6.mingle.Exceptions.UsernameAlreadyExistsException;

import java.sql.*;

public class RegistrationRepository implements Repository{

    public void checkUsernameAvailability(String username) throws UsernameAlreadyExistsException{
        Connection connection = null;
        try {
            connection = DbContext.getConnection();

            String sql = "SELECT * FROM [User] WHERE Username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);
            statement.executeQuery();

            if(statement.getResultSet().next()){
                throw new UsernameAlreadyExistsException();
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String registerUser(String username, String name, String password, String salt){
        Connection connection = null;
        String userId = null;
        try {
            connection = DbContext.getConnection();
            connection.setAutoCommit(false);

            String sql_1 = "INSERT INTO [User] (Username, Name, ImageId) VALUES (?, ?, NULL)";
            PreparedStatement statement_1 = connection.prepareStatement(sql_1, Statement.RETURN_GENERATED_KEYS);

            statement_1.setString(1, username);
            statement_1.setString(2, name);
            statement_1.executeUpdate();

            try (ResultSet rs = statement_1.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);

                    String sql_2 = "INSERT INTO Password (UserId, Hash, Salt) VALUES (?, ?, ?)";
                    PreparedStatement statement_2 = connection.prepareStatement(sql_2);

                    statement_2.setInt(1, generatedId);
                    statement_2.setString(2, password);
                    statement_2.setString(3, salt);

                    statement_2.executeUpdate();

                    userId = String.valueOf(generatedId);
                }
            }
            connection.commit();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return userId;
    }
}
