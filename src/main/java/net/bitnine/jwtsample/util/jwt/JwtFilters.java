package net.bitnine.jwtsample.util.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.bitnine.jwtsample.service.UserService;
import net.bitnine.jwtsample.util.cookie.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilters extends OncePerRequestFilter {

  private JwtUtil jwtUtil;
  private UserService service;
  private CookieUtil cookieUtil;

  @Autowired
  public JwtFilters(JwtUtil jwtUtil, UserService service,
      CookieUtil cookieUtil) {
    this.jwtUtil = jwtUtil;
    this.service = service;
    this.cookieUtil = cookieUtil;
  }




  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {

//    String authorizationHeader = httpServletRequest.getHeader("Authorization");
    Cookie header = cookieUtil.getCookie(httpServletRequest,"Header_Payload");
    Cookie signature = cookieUtil.getCookie(httpServletRequest,"Signature");
    String authorizationHeader;

    if(header == null){
      authorizationHeader = null;
    }
    else{
      authorizationHeader = header.getValue() + signature.getValue();
    }

    String token = null;
    String userName = null;

    if (authorizationHeader != null) {
      token = authorizationHeader;
      userName = jwtUtil.extractUsername(token);
    }

    if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = service.loadUserByUsername(userName);

      if (jwtUtil.validateToken(token, userDetails)) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }
}