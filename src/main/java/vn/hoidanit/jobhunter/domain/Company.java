package vn.hoidanit.jobhunter.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Table(name = "Companies")
@Entity
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "name không được để trống")
    private String name;

    @Column(columnDefinition = "MeDIUMTEXT")
    private String description;

    private String address;

    private String logo;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updateBy;

    // @PrePersist
    // public void handleBeforeCreate() {
    // this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
    // ? SecurityUtil.getCurrentUserLogin().get()
    // : "";

    // this.updatedAt = Instant.now();
    // }
}

// @PrePersist
// public void handleBeforeUpdate() {
// this.updateBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
// ? SecurityUtil.getCurrentUserLogin().get()
// : "";

// this.updatedAt = Instant.now();
// }
