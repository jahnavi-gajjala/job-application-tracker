package com.jahnavi.tracker.application;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users;
  private final BCryptPasswordEncoder passwords=new BCryptPasswordEncoder();
  public AuthController(UserRepository users){this.users=users;}
  @PostMapping("/signup") @ResponseStatus(HttpStatus.CREATED)
  public Map<String,Object> signup(@Valid @RequestBody SignupRequest r,HttpSession s){
    String email=r.email().trim().toLowerCase();
    if(users.findByEmailIgnoreCase(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account already exists with this email");
    User u=new User();u.setName(r.name().trim());u.setEmail(email);u.setPasswordHash(passwords.encode(r.password()));u=users.save(u);s.setAttribute("userId",u.getId());return profile(u);
  }
  @PostMapping("/login")
  public Map<String,Object> login(@Valid @RequestBody LoginRequest r,HttpSession s){
    User u=users.findByEmailIgnoreCase(r.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect email or password"));
    if(!passwords.matches(r.password(),u.getPasswordHash()))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect email or password");
    s.setAttribute("userId",u.getId());return profile(u);
  }
  @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) public void logout(HttpSession s){s.invalidate();}
  @GetMapping("/me") public Map<String,Object> me(HttpSession s){Object id=s.getAttribute("userId");if(!(id instanceof Long userId))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please log in to continue");return profile(users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please log in to continue")));}
  private Map<String,Object> profile(User u){return Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail());}
  public record SignupRequest(@NotBlank @Size(max=80) String name,@NotBlank @Email String email,@NotBlank @Size(min=6,max=72) String password){}
  public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
}