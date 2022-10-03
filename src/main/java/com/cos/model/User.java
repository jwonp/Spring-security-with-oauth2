package com.cos.model;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
public class User {
  @Id //primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String username;
  private String password;
  private String email;
  private String role; //ROLE_USER, ROLE_ADMIN
  private String provider;
  private String providerId;

  //private Timestamp loginDate;
  @CreationTimestamp
  private Timestamp createDate;

  @Builder
  public User(
    String username,
    String password,
    String email,
    String role,
    String provider,
    String providerId,
    Timestamp createDate
  ) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.provider = provider;
    this.providerId = providerId;
    this.createDate = createDate;
  }
}
