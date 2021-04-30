package net.bitnine.jwtsample.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class WebController {

  @GetMapping("/authenticate")
  public String loginPage() {
    return "test3";
  }
}
