package com.jahnavi.tracker.application;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {
 private final JobApplicationRepository repository; private final UserRepository users;
 public JobApplicationController(JobApplicationRepository repository,UserRepository users){this.repository=repository;this.users=users;}
 @GetMapping public List<JobApplication> all(@RequestParam(required=false) JobApplication.Status status,HttpSession s){User u=currentUser(s);return status==null?repository.findByUser(u):repository.findByUserAndStatus(u,status);}
 @GetMapping("/{id}") public JobApplication get(@PathVariable Long id,HttpSession s){return find(id,currentUser(s));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public JobApplication create(@Valid @RequestBody JobApplication a,HttpSession s){a.setUser(currentUser(s));return repository.save(a);}
 @PutMapping("/{id}") public JobApplication update(@PathVariable Long id,@Valid @RequestBody JobApplication i,HttpSession s){JobApplication a=find(id,currentUser(s));a.setCompany(i.getCompany());a.setRole(i.getRole());a.setLocation(i.getLocation());a.setJobUrl(i.getJobUrl());a.setSalaryRange(i.getSalaryRange());a.setAppliedDate(i.getAppliedDate());a.setStatus(i.getStatus());a.setNotes(i.getNotes());a.setFollowUpDate(i.getFollowUpDate());a.setInterviewDate(i.getInterviewDate());return repository.save(a);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id,HttpSession s){repository.delete(find(id,currentUser(s)));}
 @GetMapping("/summary") public Map<String,Long> summary(HttpSession s){List<JobApplication> a=repository.findByUser(currentUser(s));Map<String,Long> m=new LinkedHashMap<>();m.put("total",(long)a.size());for(JobApplication.Status x:JobApplication.Status.values())m.put(x.name().toLowerCase(),a.stream().filter(j->j.getStatus()==x).count());return m;}
 private User currentUser(HttpSession s){Object id=s.getAttribute("userId");if(!(id instanceof Long userId))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please log in to continue");return users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please log in to continue"));}
 private JobApplication find(Long id,User u){return repository.findById(id).filter(a->a.getUser()!=null&&a.getUser().getId().equals(u.getId())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Application not found"));}
}