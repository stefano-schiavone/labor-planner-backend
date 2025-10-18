package com.laborplanner.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

  @GetMapping("/users")
  public String getUserPage() {
    return "user-crud"; // Thymeleaf will look for user-crud.html in templates
  }
}
