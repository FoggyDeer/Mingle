package pjwstk.tpo_6.mingle.Exceptions;

public class InvalidLoginDataException extends Exception{
    public InvalidLoginDataException(String message) {
        super("Invalid login data");
    }
}
