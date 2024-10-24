package pjwstk.tpo_6.mingle.Exceptions;

public class NonExistentAccountException extends Exception{
    public NonExistentAccountException(){
        super("Account does not exist");
    }
}
