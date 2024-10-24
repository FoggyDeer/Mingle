package pjwstk.tpo_6.mingle.Services;

import com.google.gson.Gson;
import pjwstk.tpo_6.mingle.Repositories.UsersRepository;

public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(){
        usersRepository = new UsersRepository();
    }

    public String findUsersByPattern(String selfId, String pattern){
        return  new Gson().toJson( usersRepository.findUsersByPattern(Integer.parseInt(selfId),pattern));
    }



}
