package com.laborplanner.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

  // @Bean
  // CommandLineRunner createAccountType(AccountTypeRepository accountTypeRepository) {
  //   return args -> {
  //     // Create a new account type
  //     AccountType adminType = new AccountType("Admin");
  //     AccountType plannerType = new AccountType("Planner");
  //     AccountType viewerType = new AccountType("Viewer");

  //     accountTypeRepository.save(adminType);
  //     accountTypeRepository.save(plannerType);
  //     accountTypeRepository.save(viewerType);

  //     System.out.println("Account types created");
  //   };
  // }

  // @Bean
  // CommandLineRunner createUser(
  //     UserRepository userRepository, AccountTypeRepository accountTypeRepository) {
  //   return args -> {
  //     // Fetch admin type
  //     AccountType adminType =
  //         accountTypeRepository
  //             .findByName("Admin")
  //             .orElseThrow(() -> new RuntimeException("Admin account not found"));

  //     // Create a new user
  //     User user = new User("Mirna", "Lahmar", "lahmarmimi6@gmail.com", adminType);

  //     userRepository.save(user);

  //     System.out.println("User created: " + user);
  //   };
  // }
}
