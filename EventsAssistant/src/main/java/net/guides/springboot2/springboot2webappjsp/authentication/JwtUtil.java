package net.guides.springboot2.springboot2webappjsp.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.guides.springboot2.springboot2webappjsp.controllers.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtil {
    //the time is one hour
    public static final long EXPIRE_TIME = 60 * 60 * 1000;
    //token secret
    public static final String SECRET = "Overcoming tough time";


    public static String sign(String face_id) {

        //expire data
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        //token secret
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        String token = JWT.create()
                .withClaim("face_id", face_id)
                .withExpiresAt(date)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        return token;

    }


    //using verify whether they are the same token
    public static boolean verify(String token, String face_id) {
        try {

            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withClaim("face_id", face_id).build();
            DecodedJWT jwt = verifier.verify(token);

            System.out.println("Token verified!");
            System.out.println("Issue time: " + jwt.getIssuedAt());
            System.out.println("Expire time: " + jwt.getExpiresAt());
            System.out.println("Face id: " + jwt.getClaim("face_id"));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Result getUserFaceIdByToken(HttpServletRequest request)  {

        String token = request.getHeader("Authorization");

        DecodedJWT jwt = JWT.decode(token);
        if (jwt.getExpiresAt().before(new Date())) {
            return Result.fail("Token is expired");
        }
        return Result.succ("Face_id", jwt.getClaim("face_id").asString());
    }

}
