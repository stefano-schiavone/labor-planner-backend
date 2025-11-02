package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
public class UserEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_uuid", nullable = false, updatable = false)
  private UUID userUuid;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_type_uuid", nullable = false)
  private AccountTypeEntity type;
}
