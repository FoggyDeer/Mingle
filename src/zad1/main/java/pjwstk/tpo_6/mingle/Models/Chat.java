package pjwstk.tpo_6.mingle.Models;


public class Chat {
    int id;
    String username;
    String name;
    String lastMessage;
    String lastMessageTime;

    public Chat(int id, String username, String name, String lastMessage, String lastMessageTime) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }
}
