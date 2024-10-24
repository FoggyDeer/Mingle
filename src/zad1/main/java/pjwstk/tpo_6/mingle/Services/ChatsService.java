package pjwstk.tpo_6.mingle.Services;

import com.google.gson.Gson;
import pjwstk.tpo_6.mingle.Repositories.ChatsRepository;

public class ChatsService{
    private final ChatsRepository chatsRepository = new ChatsRepository();

    public String getChatsFor(String userId, String minutesOffset){
        return new Gson().toJson(chatsRepository.getChatsFor(Integer.parseInt(userId), Integer.parseInt(minutesOffset)));
    }
}
