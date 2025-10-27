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
  @Column(name = "account_type_uuid", nullable = false, updatable = false)
  private String accountTypeUuid = UUID.randomUUID().toString();

  @Column(name = "name", nullable = false, unique = true)
  private String name;
}
