package net.bitnine.jwtsample.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.bitnine.jwtsample.domain.AuthRequest;
import net.bitnine.jwtsample.util.cookie.CookieUtil;
import net.bitnine.jwtsample.util.jwt.JwtUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class JwtController {

  private AuthenticationManager authenticationManager;
  private JwtUtil jwtUtil;
  private CookieUtil cookieUtil;

  @Autowired
  public JwtController(
      AuthenticationManager authenticationManager, JwtUtil jwtUtil,
      CookieUtil cookieUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.cookieUtil = cookieUtil;
  }

  @GetMapping("/a")
  public String welcome() {
    return "hi";
  }

  @PostMapping("/authenticate")
  public String generateToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) throws Exception {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
            authRequest.getPassword())
    );
    String token = jwtUtil.generateToken(authRequest.getUserName());
    String[] jwtToken = token.split("\\.");
    Cookie hp = cookieUtil.createCookie("Header_Payload",jwtToken[0]+"."+jwtToken[1]+".");
    Cookie signature = cookieUtil.createCookie("Signature", jwtToken[2]);
    response.addCookie(hp);
    response.addCookie(signature);
    return "/a";
  }


}
