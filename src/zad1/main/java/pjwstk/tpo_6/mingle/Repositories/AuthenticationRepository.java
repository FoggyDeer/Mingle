package pjwstk.tpo_6.mingle.Repositories;

import pjwstk.tpo_6.mingle.Exceptions.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class AuthenticationRepository implements Repository{

    public static final int REFRESH_TOKEN_LENGTH = 60;
    public static final int REFRESH_TOKEN_ALIVE_DAYS = 10;
    public void validateRefreshToken(int userId, String refreshToken) throws InvalidRefreshTokenException, RefreshTokenExpiredException{
        Connection connection = null;
        try {
            connection = DbContext.getConnection();
            connection.setAutoCommit(false);

            String sql = "SELECT TOP 1 rt.* FROM [User] as usr JOIN Refresh_Token as rt ON usr.UserId = rt.UserId WHERE usr.UserId = ? ORDER BY rt.ExpirationDate DESC";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            boolean next = resultSet.next();
            if (!next) {
                throw new InvalidRefreshTokenException();
            }

            String token = resultSet.getString("Refresh_Token");
            boolean isToken = token.equals(refreshToken);
            if (!isToken) {
                throw new InvalidRefreshTokenException();
            }

            Timestamp expirationDate = resultSet.getTimestamp("ExpirationDate");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiration = expirationDate.toLocalDateTime();

            if(!expiration.isAfter(now)){
                throw new RefreshTokenExpiredException();
            }

            String sql_2 = "UPDATE Refresh_Token SET LastUsedDate = ? WHERE UserId = ?";
            PreparedStatement statement_2 = connection.prepareStatement(sql_2);
            statement_2.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement_2.setInt(2, userId);
            statement_2.executeUpdate();

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
    }

    public HashMap<String,String> getIdHashSalt(String username) throws NonExistentAccountException {
        Connection connection = null;
        HashMap<String,String> hashSalt = new HashMap<>();
        try {
            connection = DbContext.getConnection();
            connection.setAutoCommit(false);

            String sql = "SELECT pass.UserId, pass.Hash, pass.Salt FROM [User] as us JOIN Password as pass ON us.UserId = pass.UserId WHERE us.UserName = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new NonExistentAccountException();
            }

            String userId = resultSet.getString("UserId");
            String hash = resultSet.getString("Hash");
            String salt = resultSet.getString("Salt");

            hashSalt.put("UserId", userId);
            hashSalt.put("Hash", hash);
            hashSalt.put("Salt", salt);

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
        return hashSalt;
    }

    public void initRefreshToken(int userId, String token) throws InvalidUserIdException{
        Connection connection = null;
        try {
            connection = DbContext.getConnection();
            connection.setAutoCommit(false);

            String sql_1 = "UPDATE Refresh_Token SET Refresh_Token = ?, ExpirationDate = ?, CreationDate = ?, LastUsedDate = ? WHERE UserId = ?";
            PreparedStatement statement_1 = connection.prepareStatement(sql_1);

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime expirationDateTime = currentDateTime.plusDays(REFRESH_TOKEN_ALIVE_DAYS);
            Timestamp expirationDate = Timestamp.valueOf(expirationDateTime);

            statement_1.setString(1, token);
            statement_1.setTimestamp(2, expirationDate);
            statement_1.setTimestamp(3, currentTimestamp);
            statement_1.setTimestamp(4, currentTimestamp);
            statement_1.setInt(5, userId);


            int affectedRows = statement_1.executeUpdate();

            if(affectedRows == 0){
                String sql_2 = "INSERT INTO Refresh_Token(UserId, Refresh_Token, ExpirationDate, CreationDate, LastUsedDate) VALUES(?, ?, ?, ?, ?)";
                PreparedStatement statement_2 = connection.prepareStatement(sql_2);

                statement_2.setInt(1, userId);
                statement_2.setString(2, token);
                statement_2.setTimestamp(3, expirationDate);
                statement_2.setTimestamp(4, currentTimestamp);
                statement_2.setTimestamp(5, currentTimestamp);

                statement_2.executeUpdate();
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
    }

}
