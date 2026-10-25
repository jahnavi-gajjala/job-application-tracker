package com.jahnavi.tracker.application;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class JobApplicationController {
    private final JobApplicationRepository repository;
    public JobApplicationController(JobApplicationRepository repository){this.repository=repository;}
    @GetMapping public List<JobApplication> all(@RequestParam(required=false) JobApplication.Status status){return status==null?repository.findAll():repository.findByStatus(status);}
    @GetMapping("/{id}") public JobApplication get(@PathVariable Long id){return find(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public JobApplication create(@Valid @RequestBody JobApplication app){return repository.save(app);}
    @PutMapping("/{id}") public JobApplication update(@PathVariable Long id,@Valid @RequestBody JobApplication input){JobApplication a=find(id); a.setCompany(input.getCompany());a.setRole(input.getRole());a.setLocation(input.getLocation());a.setJobUrl(input.getJobUrl());a.setSalaryRange(input.getSalaryRange());a.setAppliedDate(input.getAppliedDate());a.setStatus(input.getStatus());a.setNotes(input.getNotes());a.setFollowUpDate(input.getFollowUpDate());a.setInterviewDate(input.getInterviewDate());return repository.save(a);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){repository.delete(find(id));}
    @GetMapping("/summary") public Map<String,Long> summary(){List<JobApplication> apps=repository.findAll();Map<String,Long> m=new LinkedHashMap<>();m.put("total",(long)apps.size());for(JobApplication.Status s:JobApplication.Status.values())m.put(s.name().toLowerCase(),apps.stream().filter(a->a.getStatus()==s).count());return m;}
    private JobApplication find(Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Application not found"));}
}
