package net.bitnine.jwtsample.util.cookie;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

  public Cookie createCookie(String cookieName, String value){
    Cookie token = new Cookie(cookieName, value);
    switch (cookieName){
      case "Header_Payload":
        token.setHttpOnly(false);
        break;
      case "Signature":
        token.setHttpOnly(true);
      break;
      default:
        break;
    }
    token.setPath("/");
    return token;
  }

  public Cookie getCookie(HttpServletRequest req, String cookieName){
    Cookie[] cookies = req.getCookies();
    if(cookies == null)
      return null;
    List<Cookie> cookie = Arrays.stream(cookies)
        .filter(data -> data.getName().equals(cookieName))
        .collect(Collectors.toList());
    if(!cookie.isEmpty()){
      return cookie.get(0);
    }
    return null;
  }
}
