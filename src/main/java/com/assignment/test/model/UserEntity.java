package com.assignment.test.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity(name = "UserEntity")
public class UserEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "email")
  private String email;

  @Column(name = "first_nm")
  private String firstName;

  @Column(name = "last_nm")
  private String lastName;

  @Column(name = "password")
  private String password;
  
  @Column(name = "profile_img")
  private String profileImage;

}
