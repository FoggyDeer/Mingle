package pjwstk.tpo_6.mingle.Services;

import io.jsonwebtoken.ExpiredJwtException;
import pjwstk.tpo_6.mingle.Exceptions.*;
import pjwstk.tpo_6.mingle.Repositories.AuthenticationRepository;
import pjwstk.tpo_6.mingle.Utils.JwtToken;
import pjwstk.tpo_6.mingle.Utils.JwtUtil;
import pjwstk.tpo_6.mingle.Utils.Sha256;
import pjwstk.tpo_6.mingle.Utils.SecureKeyGenerator;

import java.util.HashMap;

public class AuthenticationService{
    private final AuthenticationRepository authRepository;
    public AuthenticationService() {
        authRepository = new AuthenticationRepository();
    }

    public void validateTokens(String jwtToken, String refreshToken) throws ExpiredJwtException, InvalidRefreshTokenException, RefreshTokenExpiredException{
        JwtToken parsedToken = JwtUtil.parseToken(jwtToken);
        authRepository.validateRefreshToken(Integer.parseInt(parsedToken.getUserId()), refreshToken);
    }

    public HashMap<String, String> updateTokens(String jwtToken, String refreshToken) throws InvalidRefreshTokenException, RefreshTokenExpiredException, InvalidUserIdException, InternalServerErrorException {
        String userId;
        String userName;
        try {
            JwtToken jwt = JwtUtil.parseToken(jwtToken);
            userId = jwt.getUserId();
            userName = jwt.getUserName();
        }catch (ExpiredJwtException e){
            userId = e.getClaims().getSubject();
            userName = e.getClaims().get("username", String.class);
        }

        authRepository.validateRefreshToken(Integer.parseInt(userId), refreshToken);

        HashMap<String, String> newTokens = generateTokens(userId, userName);
        authRepository.initRefreshToken(Integer.parseInt(userId), newTokens.get("rfrt"));

        return newTokens;
    }

    public HashMap<String, String> generateTokens(String userId, String userName) {
        String newToken = JwtUtil.generateToken(userId, userName);
        String newRefreshToken = SecureKeyGenerator.generateKey(AuthenticationRepository.REFRESH_TOKEN_LENGTH);

        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("jwt", newToken);
        tokens.put("rfrt", newRefreshToken);
        return tokens;
    }

    public void initRefreshToken(String userId, String refreshToken) throws InvalidUserIdException {
        authRepository.initRefreshToken(Integer.parseInt(userId), refreshToken);
    }

    public HashMap<String, String> login(String username, String password) throws InvalidPasswordException, NonExistentAccountException, InvalidRefreshTokenException, RefreshTokenExpiredException, InvalidUserIdException, InternalServerErrorException, InvalidUsernameException {
        if(username == null || username.isEmpty())
            throw new InvalidUsernameException();

        HashMap<String, String> hashSalt = authRepository.getIdHashSalt(username);

        if(hashSalt == null || hashSalt.isEmpty() || hashSalt.get("Hash") == null || hashSalt.get("Salt") == null)
            throw new NonExistentAccountException();

        String userId = hashSalt.get("UserId");
        String hash = hashSalt.get("Hash");
        String salt = hashSalt.get("Salt");

        String hashedPassword = Sha256.hash(password + salt);

        if(!hash.equals(hashedPassword))
            throw new InvalidPasswordException();

        String jwtToken = JwtUtil.generateToken(userId, username);
        String refreshToken = SecureKeyGenerator.generateKey(AuthenticationRepository.REFRESH_TOKEN_LENGTH);

        authRepository.initRefreshToken(Integer.parseInt(userId), refreshToken);

        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("jwt", jwtToken);
        tokens.put("rfrt", refreshToken);
        return tokens;
    }
}
