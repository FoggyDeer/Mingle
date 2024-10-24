package pjwstk.tpo_6.mingle.Exceptions;

public class InvalidRefreshTokenException extends Exception{
    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
