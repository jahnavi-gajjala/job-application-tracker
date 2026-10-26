package com.jahnavi.tracker.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
public class JobApplication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String company; @NotBlank private String role; private String location; private String jobUrl; private String salaryRange; private LocalDate appliedDate;
 @Enumerated(EnumType.STRING) private Status status=Status.APPLIED; @Column(length=2000) private String notes; private LocalDate followUpDate; private LocalDate interviewDate;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id") @JsonIgnore private User user;
 public enum Status{SAVED,APPLIED,ASSESSMENT,INTERVIEW,OFFER,REJECTED,WITHDRAWN}
 public Long getId(){return id;} public String getCompany(){return company;} public void setCompany(String v){company=v;} public String getRole(){return role;} public void setRole(String v){role=v;} public String getLocation(){return location;} public void setLocation(String v){location=v;} public String getJobUrl(){return jobUrl;} public void setJobUrl(String v){jobUrl=v;} public String getSalaryRange(){return salaryRange;} public void setSalaryRange(String v){salaryRange=v;} public LocalDate getAppliedDate(){return appliedDate;} public void setAppliedDate(LocalDate v){appliedDate=v;} public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public LocalDate getFollowUpDate(){return followUpDate;} public void setFollowUpDate(LocalDate v){followUpDate=v;} public LocalDate getInterviewDate(){return interviewDate;} public void setInterviewDate(LocalDate v){interviewDate=v;} public User getUser(){return user;} public void setUser(User v){user=v;}
}