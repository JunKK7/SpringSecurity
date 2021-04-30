package net.bitnine.config;

import net.bitnine.jwtsample.util.jwt.JwtFilters;
import net.bitnine.jwtsample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private JwtFilters jwtFilters;
  private UserService userService;

  @Autowired
  public SecurityConfig(JwtFilters jwtFilters, UserService userService) {
    this.jwtFilters = jwtFilters;
    this.userService = userService;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService);
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /**
   * authorizeRequests() -> 시큐리티 처리에 HttpServletRequest 사용 antMatchers() -> 특정한 경로 지정 참고
   * https://velog.io/@jayjay28/2019-09-04-1109-%EC%9E%91%EC%84%B1%EB%90%A8 sessionManagement()
   * .sessionCreationPolicy(SessionCreationPolicy.STATELESS) -> 세션을 생성하지도 않고 기존것을 사용하지 않음( JWT 사용 )
   *
   * @param http http param
   * @throws Exception
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests().antMatchers("/authenticate").permitAll()
        .antMatchers("/login").permitAll()
        .anyRequest().authenticated()
        .and().exceptionHandling().and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilterBefore(jwtFilters, UsernamePasswordAuthenticationFilter.class);
  }
}
