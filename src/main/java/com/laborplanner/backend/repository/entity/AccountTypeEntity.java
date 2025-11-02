package com.laborplanner.backend.repository.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

// Lombok and JPA notation
@Entity
@Table(name = "account_type")
@Getter
@Setter
@NoArgsConstructor // Required by JPA
@AllArgsConstructor
public class AccountTypeEntity {

  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "account_type_uuid", nullable = false, updatable = false)
  private UUID accountTypeUuid;

  @Column(name = "name", nullable = false, unique = true)
  private String name;
}
