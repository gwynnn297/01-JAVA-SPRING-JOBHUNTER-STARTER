package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;
   private String name;
   private String location;
   private double salary;
   private int quantity;
   private LevelEnum level;

   @Column(columnDefinition = "MEDIUMTEXT")
   private String description;

   private Instant startDate;
   private Instant endDate;
   private boolean active;
   private Instant createdAt;
   private Instant updatedAt;
   private String createdBy;
   private String updatedBy;

   @ManyToOne
   @JoinColumn(name = "company_id")
   private Company company;
   // fFetchType.LAZY : chỉ load khi gọi đến
   @ManyToMany(fetch = FetchType.LAZY)
   @JsonIgnoreProperties(value = { "jobs" })
   // tạo 1 bảng mới job_skill có 2 khóa ngoại
   @JoinTable(name = "job_skill", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
   private List<Skill> skills;
}
