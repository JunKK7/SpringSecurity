package net.bitnine.jwtsample.controller;

import lombok.extern.slf4j.Slf4j;
import net.bitnine.jwtsample.domain.AuthRequest;
import net.bitnine.jwtsample.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
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

  @Autowired
  public JwtController(
      AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }




  @GetMapping("/a")
  public String welcome(){
    return "hi";
  }

  @PostMapping("/authenticate")
  public String generateToken(@RequestBody AuthRequest authRequest) throws Exception{
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
      );
    } catch (Exception ex) {
      log.info(ex.toString());
      //throw new Exception("inavalid username/password");
    }
    return jwtUtil.generateToken(authRequest.getUserName());
  }
}
