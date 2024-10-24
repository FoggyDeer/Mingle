package pjwstk.tpo_6.mingle.Repositories;

import pjwstk.tpo_6.mingle.Models.MessageInstance;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

public class MessagesRepository implements Repository {
    public MessagesRepository() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
    public ArrayList<MessageInstance> getMessagesFor(int selfId, String username, int minutesOffset, LocalDateTime timeFrom, LocalDateTime timeTo ){
        ArrayList<MessageInstance> messageInstances = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DbContext.getConnection();

            String sql_1 = "SELECT cu1.ChatId FROM Chat_User cu1 JOIN Chat_User cu2 ON cu1.ChatId = cu2.ChatId JOIN [User] u1 ON cu1.UserId = u1.UserId JOIN [User] u2 ON cu2.UserId = u2.UserId WHERE u1.UserId = ? AND u2.Username = ?;";
            PreparedStatement statement_1 = connection.prepareStatement(sql_1);

            statement_1.setInt(1, selfId);
            statement_1.setString(2, username);
            ResultSet resultSet_1 = statement_1.executeQuery();
            if (resultSet_1.next()){
                int chatId = resultSet_1.getInt(1);
                String sql_2 = "SELECT MessageId, SenderId, Text, DATEADD(minute, DATEPART(tzoffset, Time) - ?, Time) FROM Message WHERE ChatId = ?";
                if (timeFrom != null){
                    sql_2 += " AND Time >= ?";
                }
                if (timeTo != null){
                    sql_2 += " AND Time <= ?";
                }
                PreparedStatement statement_2 = connection.prepareStatement(sql_2);
                statement_2.setInt(1, minutesOffset);
                statement_2.setInt(2, chatId);

                if (timeFrom != null){
                    statement_2.setTimestamp(3, Timestamp.valueOf(timeFrom));
                    if (timeTo != null){
                        statement_2.setTimestamp(4, Timestamp.valueOf(timeTo));
                    }
                } else if (timeTo != null){
                    statement_2.setTimestamp(3, Timestamp.valueOf(timeTo));
                }

                ResultSet resultSet_2 = statement_2.executeQuery();
                while (resultSet_2.next()) {
                    messageInstances.add(new MessageInstance(
                            resultSet_2.getInt(1),
                            resultSet_2.getInt(2),
                            resultSet_2.getString(3),
                            resultSet_2.getTimestamp(4).toString()
                    ));
                }
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
        return messageInstances;
    }

    public void sendMessage(int selfId, String username, String message){
        if(message.length() > 1024)
            message = message.substring(0, 1024);
        Connection connection = null;
        try {
            connection = DbContext.getConnection();
            connection.setAutoCommit(false);

            String sql_1 = "SELECT cu1.ChatId FROM Chat_User cu1 JOIN Chat_User cu2 ON cu1.ChatId = cu2.ChatId JOIN [User] u1 ON cu1.UserId = u1.UserId JOIN [User] u2 ON cu2.UserId = u2.UserId WHERE u1.UserId = ? AND u2.Username = ?;";
            PreparedStatement statement_1 = connection.prepareStatement(sql_1);

            statement_1.setInt(1, selfId);
            statement_1.setString(2, username);
            ResultSet resultSet_1 = statement_1.executeQuery();

            Integer chatId = null;

            if (!resultSet_1.next()){
                String sql_2 = "INSERT INTO Chat (Type) VALUES ('Private')";
                PreparedStatement statement_2 = connection.prepareStatement(sql_2, Statement.RETURN_GENERATED_KEYS);
                statement_2.executeUpdate();

                try(ResultSet rs = statement_2.getGeneratedKeys()){
                    if (rs.next()) {
                        chatId = rs.getInt(1);
                        String sql_3 = "INSERT INTO Chat_User (ChatId, UserId) VALUES (?, ?)";
                        PreparedStatement statement_3 = connection.prepareStatement(sql_3);
                        statement_3.setInt(1, chatId);
                        statement_3.setInt(2, selfId);
                        statement_3.executeUpdate();

                        String sql_4 = "INSERT INTO Chat_User (ChatId, UserId) VALUES (?, (SELECT UserId FROM [User] WHERE Username = ?))";
                        PreparedStatement statement_4 = connection.prepareStatement(sql_4);
                        statement_4.setInt(1, chatId);
                        statement_4.setString(2, username);
                        statement_4.executeUpdate();
                    }
                }
            } else {
                chatId = resultSet_1.getInt(1);
            }

            int rowsAffected;
            if (chatId != null){
                String sql_5 = "INSERT INTO Message (MessageId, ChatId, SenderId, Text) VALUES (ISNULL((SELECT MAX(MessageId) FROM Message WHERE ChatId = ?), 0) + 1, ?, ?, ?)";
                PreparedStatement statement_5 = connection.prepareStatement(sql_5);
                statement_5.setInt(1, chatId);
                statement_5.setInt(2, chatId);
                statement_5.setInt(3, selfId);
                statement_5.setString(4, message);
                rowsAffected = statement_5.executeUpdate();
            } else {
                throw new SQLException("ChatId is null");
            }

            if (rowsAffected == 0){
                throw new SQLException("No rows affected");
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
