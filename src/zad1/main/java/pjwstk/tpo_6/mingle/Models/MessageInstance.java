package pjwstk.tpo_6.mingle.Models;

public class MessageInstance {
    int id;
    int senderId;
    String text;
    String time;

    public MessageInstance(int id, int senderId, String text, String time){
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.time = time;
    }
}
