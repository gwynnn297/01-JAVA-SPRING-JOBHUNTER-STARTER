package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    // một công ty thì có nhiều users
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users;

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
