package pjwstk.tpo_6.mingle.Services;

import com.google.gson.Gson;
import pjwstk.tpo_6.mingle.Exceptions.BadRequestError;
import pjwstk.tpo_6.mingle.Repositories.MessagesRepository;

import java.time.LocalDateTime;

public class MessagesService {
    private final MessagesRepository messagesRepository = new MessagesRepository();
    public String getMessagesFor(String selfId, String username, String minutesOffset, String timeFrom, String timeTo){
        LocalDateTime timeFromParsed = timeFrom == null ? null : LocalDateTime.parse(timeFrom);
        LocalDateTime timeToParsed = timeTo == null ? null : LocalDateTime.parse(timeTo);
        return new Gson().toJson(messagesRepository.getMessagesFor(Integer.parseInt(selfId), username, Integer.parseInt(minutesOffset), timeFromParsed, timeToParsed));
    }

    public void sendMessage(String selfId, String username, String message) throws BadRequestError {
        if(selfId == null || selfId.isEmpty() || username == null || username.isEmpty() || message == null || message.isEmpty())
            throw new BadRequestError();
        messagesRepository.sendMessage(Integer.parseInt(selfId), username, message);
    }
}
