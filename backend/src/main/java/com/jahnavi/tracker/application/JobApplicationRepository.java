package com.jahnavi.tracker.application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
  List<JobApplication> findByUser(User user);
  List<JobApplication> findByUserAndStatus(User user, JobApplication.Status status);
}