package vn.hoidanit.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @NotBlank(message = "email không được để trống")
    private String email;
    @NotBlank(message = "password không được để trống")
    private String password;

    // nhiều nhân viên thuộc 1 công ty
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private int age;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender; // MALE/FEMALE
    private String address;
    @Column(columnDefinition = "MeDIUMTEXT")
    private String refreshToken;
    private Instant createdAt;

    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    

    // @PrePersist
    // public void handleBeforeCreate() {
    // this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent
    // ? SecurityUtil.getCurrentUserLogin().get()
    // : "";
    // this.createdAt = Instant.now();

    // }

}
