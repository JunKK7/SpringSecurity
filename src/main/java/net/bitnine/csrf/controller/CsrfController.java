package net.bitnine.csrf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CsrfController {

  /*@RequestMapping("/")
  public String testGet(){
    return "test";
  }

  @PostMapping("/test")
  public String testPost(){
    return "test2";
  }*/
  @GetMapping("/")
  public void testGet(){

  }
  @PostMapping("/")
  public void testPost(){

  }
}
