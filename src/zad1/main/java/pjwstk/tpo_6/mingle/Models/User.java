package pjwstk.tpo_6.mingle.Models;

public class User{
    int id;
    String username;
    String name;

    public User(int id, String username, String name){
        this.id = id;
        this.username = "@" + username;
        this.name = name;
    }
}