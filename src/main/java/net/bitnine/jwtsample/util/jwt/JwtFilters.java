package net.bitnine.jwtsample.util.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.bitnine.jwtsample.util.redis.RedisUtil;
import net.bitnine.jwtsample.service.UserService;
import net.bitnine.jwtsample.util.cookie.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtFilters extends OncePerRequestFilter {

  private JwtUtil jwtUtil;
  private UserService service;
  private CookieUtil cookieUtil;
  private RedisUtil redisUtil;

  @Autowired
  public JwtFilters(JwtUtil jwtUtil, UserService service,
      CookieUtil cookieUtil, RedisUtil redisUtil) {
    this.jwtUtil = jwtUtil;
    this.service = service;
    this.cookieUtil = cookieUtil;
    this.redisUtil = redisUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {

    Cookie header = cookieUtil.getCookie(httpServletRequest, "Header_Payload");
    Cookie signature = cookieUtil.getCookie(httpServletRequest, "Signature");

    String CookieValue;
    String token = null;
    String userName = null;

    if (header == null || signature == null) {
      CookieValue = null;
    } else {
      CookieValue = header.getValue() + signature.getValue();
    }

    if (CookieValue != null) {
      token = CookieValue;
      userName = jwtUtil.extractUsername(token);
    }

    /**
     * JWT 토큰 인증 / 인가
     */
    try{
      if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = service.loadUserByUsername(userName);
        if (jwtUtil.validateToken(token, userDetails)) {
          Auth(userDetails,httpServletRequest);
        }
      }
    } catch (Exception e){
      log.warn(String.valueOf(e));
      if(redisUtil.findRefreshToken(userName)){
        UserDetails userDetails = service.loadUserByUsername(userName);
        // SPLIT TOKEN
        String[] jwtToken = jwtUtil.generateToken(userName, jwtUtil.getACCESS_TOKEN()).split("\\.");

        // CREATE COOKIE
        Cookie hp = cookieUtil.createCookie("Header_Payload", jwtToken[0] + "." + jwtToken[1] + ".");
        Cookie sg = cookieUtil.createCookie("Signature", jwtToken[2]);

        Auth(userDetails,httpServletRequest);

        httpServletResponse.addCookie(hp);
        httpServletResponse.addCookie(sg);
        log.info("CREATE NEW ACCESS TOKEN");
      }else{
        log.info("NO REFRESH TOKEN");
      }
    }

    /**
     * Refresh 토큰 확인 후 Access 재 생성
     *//*
    try{
      if(userName != null && redisUtil.findRefreshToken(userName)){
        UserDetails userDetails = service.loadUserByUsername(userName);
        // SPLIT TOKEN
        String[] jwtToken = jwtUtil.generateToken(userName, jwtUtil.getACCESS_TOKEN()).split("\\.");

        // CREATE COOKIE
        Cookie hp = cookieUtil.createCookie("Header_Payload", jwtToken[0] + "." + jwtToken[1] + ".");
        Cookie sg = cookieUtil.createCookie("Signature", jwtToken[2]);

        Auth(userDetails,httpServletRequest);

        httpServletResponse.addCookie(hp);
        httpServletResponse.addCookie(sg);
      }
    } catch (Exception e){
      log.error(String.valueOf(e));
    }*/

    /**
     * Filtering
     */
    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  public void Auth(UserDetails userDetails, HttpServletRequest httpServletRequest){
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
  }
}