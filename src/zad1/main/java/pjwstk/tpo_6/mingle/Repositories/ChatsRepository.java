package pjwstk.tpo_6.mingle.Repositories;

import pjwstk.tpo_6.mingle.Models.Chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimeZone;

public class ChatsRepository {

    public ChatsRepository() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
    public ArrayList<Chat> getChatsFor(int userId, int minutesOffset){
        ArrayList<Chat> chats = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DbContext.getConnection();

            String sql = "SELECT cu.ChatId, u.Username, u.Name, Text, DATEADD(minute, DATEPART(tzoffset, Time) - ?, Time) FROM Chat_User cu JOIN [USER] u ON cu.UserId = u.UserId JOIN (SELECT ChatId, Text, Time FROM Message m_1 WHERE MessageId = (SELECT MAX(MessageId) FROM Message WHERE ChatId = m_1.ChatId GROUP BY ChatId)) AS m ON cu.ChatId = m.ChatId WHERE cu.ChatId IN (Select ChatId FROM Chat_User WHERE UserId = ?) AND cu.UserId != ? ORDER BY Time DESC";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, minutesOffset);
            statement.setInt(2, userId);
            statement.setInt(3, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                chats.add(new Chat(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getTimestamp(5).toLocalDateTime().toString()
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
        return chats;
    }
}
