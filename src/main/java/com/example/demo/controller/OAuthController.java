package com.example.demo.controller;

import com.example.demo.dto.OAuthAccessToken;
import com.example.demo.dto.UserDTOLogin;
import com.example.demo.repository.PartyAppRepository;
import com.example.demo.service.AuthorizationService;
import com.example.demo.service.UserControllerDTO;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.demo.note.StaticVariables.*;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final PartyAppRepository repository;
    private final UserControllerDTO userControllerDTO;
    private final AuthorizationService authorizationService;


    public OAuthController(UserControllerDTO userController, PartyAppRepository repository, AuthorizationService authorizationService) {
        this.repository = repository;
        this.userControllerDTO = userController;
        this.authorizationService = authorizationService;
    }

    @RequestMapping(value = "/accessToken", method = RequestMethod.GET)
    public Object accessToken(@RequestParam("auth_code") String authCode, HttpSession session) {
        if (!authCode.equals(AUTH_CODE)) {
            return "unAuthorization";
        }
        AUTH_CODE = null;
        return createAccessToken(session);
    }

    private int validToken(String Access_Token, String session) {
        try {
            JWTClaimsSet.Builder claimSet = new JWTClaimsSet.Builder();
            JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);
            new EncryptedJWT(jweHeader, claimSet.build());
            EncryptedJWT jwt;
            jwt = EncryptedJWT.parse(Access_Token);
            RSADecrypter decrypter = new RSADecrypter(PRIVATE_KEY);
            jwt.decrypt(decrypter);

            System.out.println("===================@#=============================");
            System.out.println("Issuer: [ " + jwt.getJWTClaimsSet().getIssuer() + "]");
            System.out.println("Issuer: [ " + jwt.getJWTClaimsSet().getAudience() + "]");
            System.out.println("Issuer: [ " + jwt.getJWTClaimsSet().getExpirationTime() + "]");

            String sessionId = (String) jwt.getJWTClaimsSet().getClaim("sessionId");
            if(!session.equals(sessionId)){
                return -1;
            }


            //EXP
            Date date = jwt.getJWTClaimsSet().getExpirationTime();
            Date date1 = new Date();
            long mili = date.getTime();
            System.out.println(mili);

            long mili1 = date1.getTime();
            System.out.println(mili1);
            if (mili - mili1 <= 0) {
                return 0;
            }

        } catch (ParseException | JOSEException e) {
            return -1;
        }
        return 1;
    }

    @RequestMapping(value = "/authCode", method = RequestMethod.POST)
    public String OAuth(@RequestParam("client_id") int clientId, @RequestParam("client_secret") String clientSecret,
                        @RequestBody UserDTOLogin userDTOLogin, HttpSession session,
                        @RequestParam("grant_type") String grantType) {
        if(!authorizationService.login(session, userDTOLogin)){
            return "UnAuthenticate";
        }
        if (repository.findByAppId(clientId) == null || !repository.findByAppId(clientId).getAppSecret().equals(clientSecret)) {
            return "Unsigned app!";
        }
        if(!grantType.equals("code")){
            return "unsupported grant type";
        }
        AUTH_CODE = randomString(64);
        return AUTH_CODE;
    }

    @RequestMapping(value = "/findAllProduct", method = RequestMethod.GET)
    public List<Object> findAllProduct(@RequestParam("access_token") String accessToken, @RequestParam("clientId") int clientId,
                                       @RequestParam("clientSecret") String clientSecret, HttpSession session) {
        if (repository.findByAppId(clientId) == null || !repository.findByAppId(clientId).getAppSecret().equals(clientSecret)) {
            return Collections.singletonList("Wrong tho");
        }
        if (validToken(accessToken, session.getId()) == -1 || validToken(accessToken, session.getId()) == 0) {
            return Collections.singletonList("Invalid token");
        }
        List<Object> variable = (List<Object>) (List<?>) userControllerDTO.findAllProduct();
        return variable;
    }

    @RequestMapping("/findCurrent")
    public Object findCurrentUser(@RequestParam("access_token") String accessToken, @RequestParam("client_id") int clientId,
                                   @RequestParam("client_secret") String clientSecret, HttpSession session){
        if (repository.findByAppId(clientId) == null || !repository.findByAppId(clientId).getAppSecret().equals(clientSecret)
            || validToken(accessToken, session.getId()) == -1 || validToken(accessToken, session.getId()) == 0) {
            return "UnAuthorization";
        }
        return authorizationService.findCurrentUser(session);
    }


    @RequestMapping(value = "/refreshToken",method = RequestMethod.GET)
    public Object refreshToken(@RequestParam("grant_type") String grant_type, @RequestParam("access_token") String accessToken,
                               HttpSession session){
        if(!grant_type.equals("refreshToken")){
            return "UNSUPPORTED GRANT TYPE";
        }
        if(validToken(accessToken ,session.getId()) == -1){
            return "UnAuthorization";
        }
        return createAccessToken(session);
    }

    public Object createAccessToken(HttpSession session){
        String jwtString = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

            PUBLIC_KEY = rsaPublicKey;
            PRIVATE_KEY = rsaPrivateKey;

            JWTClaimsSet.Builder claimSet = new JWTClaimsSet.Builder();
            claimSet.issuer("localhost://oauth");
            claimSet.subject("Tuan Tran");
            claimSet.audience("My audience");
            claimSet.expirationTime(new Date(new Date().getTime() + 1000 * 10 * 60));
            claimSet.notBeforeTime(new Date());
            claimSet.jwtID(UUID.randomUUID().toString());
            claimSet.claim("sessionId",session.getId());

            System.out.println("---------------------------");
            System.out.println("Claim set: \n" + claimSet.build());

            JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);
            EncryptedJWT jwt = new EncryptedJWT(jweHeader, claimSet.build());

            RSAEncrypter rsaEncrypter = new RSAEncrypter(rsaPublicKey);
            jwt.encrypt(rsaEncrypter);

            jwtString = jwt.serialize();
            System.out.println("\nJWT compact form: " + jwtString);

            jwt = EncryptedJWT.parse(jwtString);
            RSADecrypter rsaDecrypter = new RSADecrypter(rsaPrivateKey);

            jwt.decrypt(rsaDecrypter);

            System.out.println("================================================");
            System.out.println("Issuer: [ " + jwt.getJWTClaimsSet().getIssuer() + "]");
            System.out.println("Subject: [" + jwt.getJWTClaimsSet().getSubject() + "]");
            System.out.println("Audience size: [" + jwt.getJWTClaimsSet().getAudience().size() + "]");
            System.out.println("Expiration Time: [" + jwt.getJWTClaimsSet().getExpirationTime() + "]");
            System.out.println("Not Before Time: [" + jwt.getJWTClaimsSet().getNotBeforeTime() + "]");
            System.out.println("Issue At: [" + jwt.getJWTClaimsSet().getIssueTime() + "]");
            System.out.println("JWT ID: [" + jwt.getJWTClaimsSet().getJWTID() + "]");
            System.out.println("JWT SessionId: [" + jwt.getJWTClaimsSet().getStringClaim("sessionId") + "]");


        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e);
        } catch (JOSEException | ParseException e) {
            e.printStackTrace();
        }
        OAuthAccessToken accessToken = new OAuthAccessToken();
        accessToken.setAccessToken(jwtString);
        accessToken.setExpireIn(600);
        accessToken.setRefreshToken("REFRESH_TOKEN");
        return accessToken;
    }

    @RequestMapping("/test")
    public String test(){
        return randomString(36);
    }
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private String randomString(int len){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

}
