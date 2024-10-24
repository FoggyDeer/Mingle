package pjwstk.tpo_6.mingle.Repositories;

import pjwstk.tpo_6.mingle.Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsersRepository implements Repository{

    public ArrayList<User> findUsersByPattern(int selfId, String pattern){
        ArrayList<User> users = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DbContext.getConnection();

            String sql = "SELECT TOP 20 UserId, Username, Name FROM [User] WHERE Username LIKE ? AND UserId != ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, pattern + "%");
            statement.setInt(2, selfId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                users.add(new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                ));
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
        return users;
    }

    public String getName(int userId){
        String name = "";
        Connection connection = null;
        try {
            connection = DbContext.getConnection();

            String sql = "SELECT Name FROM [User] WHERE UserId = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                name = resultSet.getString(1);
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
        return name;
    }
}
