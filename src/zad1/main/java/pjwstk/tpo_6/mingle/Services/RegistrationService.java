package pjwstk.tpo_6.mingle.Services;

import pjwstk.tpo_6.mingle.Exceptions.*;
import pjwstk.tpo_6.mingle.Repositories.RegistrationRepository;
import pjwstk.tpo_6.mingle.Utils.SecureKeyGenerator;
import pjwstk.tpo_6.mingle.Utils.Sha256;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegistrationService{
    private static final int SALT_LENGTH = 36;
    private final AuthenticationService authService = new AuthenticationService();
    private final RegistrationRepository registrationRepository;

    public RegistrationService() {
        registrationRepository = new RegistrationRepository();
    }

    public void validateUsername(String username) throws UsernameAlreadyExistsException, InvalidUsernameException {
        if(!isUsernameValid(username))
            throw new InvalidUsernameException();
        registrationRepository.checkUsernameAvailability(username);
    }

    public HashMap<String, String> registerUser(String username, String name, String password) throws InvalidUserIdException, InvalidUsernameException, BadRequestError {
        if(!isUsernameValid(username))
            throw new InvalidUsernameException();
        if(!isNameValid(name) || password == null || password.length() < 10)
            throw new BadRequestError();

        String salt = SecureKeyGenerator.generateKey(SALT_LENGTH);
        password = Sha256.hash(password + salt);
        String userId = registrationRepository.registerUser(username, name, password, salt);
        HashMap<String, String> tokens = authService.generateTokens(userId, username);
        authService.initRefreshToken(userId, tokens.get("rfrt"));
        return tokens;
    }

    public boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]*$");
        boolean isValid = pattern.matcher(username).matches();

        return isValid && username.length() >= 3 & username.length() <= 25;
    }

    public boolean isNameValid(String name) {
        return name != null && !name.isEmpty();
    }
}
