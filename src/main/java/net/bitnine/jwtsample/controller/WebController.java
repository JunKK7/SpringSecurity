package net.bitnine.jwtsample.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import net.bitnine.jwtsample.domain.AuthRequest;
import net.bitnine.jwtsample.util.cookie.CookieUtil;
import net.bitnine.jwtsample.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class WebController {
  private AuthenticationManager authenticationManager;
  private JwtUtil jwtUtil;
  private CookieUtil cookieUtil;

  @Autowired
  public WebController(
      AuthenticationManager authenticationManager, JwtUtil jwtUtil,
      CookieUtil cookieUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.cookieUtil = cookieUtil;
  }

  @GetMapping("/authenticate")
  public String loginPage(){
    return "test3";
  }
}
