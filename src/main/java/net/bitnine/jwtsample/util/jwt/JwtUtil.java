package net.bitnine.jwtsample.util.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Getter
public class JwtUtil {
  private final long ACCESS_TOKEN_SECOND = 1000L * 60 * 1;
  //private final long REFRESH_TOKEN_SECOND = 1000L * 60 * 60 * 24 * 10;
  private final long REFRESH_TOKEN_SECOND = 1000L * 60 * 2;
  private final int ACCESS_TOKEN = 0;
  private final int REFRESH_TOKEN = 1;
  private String secret = "BitnineDEV";

  public String extractUsername(String token) {
    //return extractClaim(token, Claims::getSubject);
    String payLoad = new String(Base64.getDecoder().decode(token.split("\\.")[1].getBytes()));
    Gson gson = new Gson();
    Map<String,Object> map = new HashMap<String,Object>();
    map = (Map<String,Object>) gson.fromJson(payLoad,map.getClass());
    return map.get("sub").toString();
  }

  /**
   * 만료 로직
   *
   * @param token
   * @return
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }


  public String generateToken(String username, int tokenType) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, username, tokenType);
  }

  private String createToken(Map<String, Object> claims, String subject, int tokenType) {
    switch (tokenType){
      case ACCESS_TOKEN:
        return Jwts.builder().setClaims(claims).setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_SECOND))
            .signWith(SignatureAlgorithm.HS256, secret).compact();
      case REFRESH_TOKEN:
        return Jwts.builder().setClaims(claims).setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_SECOND))
            .signWith(SignatureAlgorithm.HS256, secret).compact();
      default:
        return null;
    }

  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
