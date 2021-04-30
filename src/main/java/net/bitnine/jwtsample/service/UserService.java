package net.bitnine.jwtsample.service;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import net.bitnine.jwtsample.domain.User;
import net.bitnine.jwtsample.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Spring Security 버전이 상승에 따른 PasswordEncoder 에러 방지 {noop} + password
   * ID / PASSWORD Check
   */
  @Override
  public UserDetails loadUserByUsername(String userName) {
    User user = userRepository.findByUserName(userName);
    return new org.springframework.security.core.userdetails.User(user.getUserName(),
        "{noop}" + user.getPassword(), new ArrayList<>());
  }
}
