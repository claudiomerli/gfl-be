package it.xtreamdev.gestioneattivita.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtils {

    private static final String key = "6SCSj39TF74qAgL4SYM2BnLJkuLZeyBg";

    public static String createJwtCustomerCodeFromContentId(Integer id) {
        Algorithm algorithm = Algorithm.HMAC256(key);


        long now = new Date().getTime();
        long nowPlus100Years = now + 3154000000000L;

        return JWT.create().withIssuedAt(new Date(now)).withExpiresAt(new Date(nowPlus100Years)).withClaim("content_id", id).sign(algorithm);
    }

    public static void verifyJwt(String jwt, Integer contentId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();


            DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
            Integer contentIdFromToken = Integer.parseInt(decodedJWT.getClaim("content_id").as(String.class));

            if (!contentIdFromToken.equals(contentId)) {
                throw new RuntimeException("Token not valid");
            }

        } catch (Exception e) {
            throw new RuntimeException("Token not valid");
        }
    }

}
