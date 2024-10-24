package pjwstk.tpo_6.mingle.Exceptions;

public class RefreshTokenExpiredException extends Exception{
    public RefreshTokenExpiredException() {
        super("Refresh token expired");
    }
}
