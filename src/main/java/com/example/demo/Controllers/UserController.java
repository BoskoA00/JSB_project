    package com.example.demo.Controllers;

    import java.util.List;
    import java.util.Optional;

    import com.example.demo.DTOS.*;
    import com.example.demo.Exceptions.DatabaseErrorException;
    import com.example.demo.Exceptions.ResponseMessage;
    import com.example.demo.Exceptions.UserExistsException;
    import com.example.demo.Exceptions.UserNotFoundException;
    import com.example.demo.Models.User;
    import com.example.demo.Services.UserService;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping(path = "api/users")
    public class UserController {

        private final UserService userService;
        @Autowired
        public UserController(UserService userService) {
            this.userService = userService;
        }

        private String[] getEmailAndRoleFromToken(String token) {
            String email = Jwts.parser()
                    .setSigningKey("**")
                    .parseClaimsJws(token)
                    .getBody()
                    .get("email", String.class);

            String role = Jwts.parser()
                    .setSigningKey("**")
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);

            return new String[]{email, role};
        }
        @GetMapping("")
        public ResponseEntity<?> GetUsers() {
            try{
                List<User> users = userService.getUsers();
                if(users.isEmpty()){
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("There are no registered users."));
                }
                else{
                    return  ResponseEntity.ok(users);
                }

            } catch (DatabaseErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        @GetMapping("{userId}")
        public ResponseEntity<?> GetUserById(@PathVariable Long userId){

            try{
            Optional<User> user = userService.GetUserById(userId);
            return ResponseEntity.ok(user);
            } catch (UserNotFoundException e) {
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            catch (DatabaseErrorException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        @GetMapping("/getByEmail/{userEmail}")
        public ResponseEntity<?> GetUserByEmail(@PathVariable String userEmail) {
            try {
                Optional<User> user = userService.GetUserByEmail(userEmail);
                return ResponseEntity.ok(user);
            } catch (UserNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (DatabaseErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }


        @PostMapping("register")
        public ResponseEntity<?> CreateUser(@RequestBody UserCreationDTO request) {
            try {
                BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder(10);
                User user = new User();
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole(request.getRole());
                user.setEmail(request.getEmail());

                User createdUser = userService.CreateUser(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            } catch (UserExistsException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            catch (DatabaseErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        @PostMapping("login")
        public ResponseEntity<?> login(@RequestBody UserLoginDTO request) {
            try {
                User user = userService.GetUserByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Invalid credentials"));
                }

                String token = Jwts.builder()
                        .setSubject(user.getEmail()).claim("email",user.getEmail())
                        .claim("role",user.getRole().toString())
                        .claim("email",user.getEmail())
                        .claim("userId",user.getId().toString())
                        .signWith(SignatureAlgorithm.HS256, "**")
                        .compact();

                UserDetailDTO responseUser = new UserDetailDTO(user.getId(), user.getFirstName(), user.getLastName(),user.getEmail(),user.getRole());

                return ResponseEntity.ok(new LoginResponseDTO(responseUser, token));
            } catch (UserNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        @DeleteMapping("{userId}")
        public ResponseEntity<?> DeleteUserById(@PathVariable Long userId,@RequestHeader("Authorization") String authorizationHeader){
            try {

                String token = authorizationHeader.substring(7);
                String[] emailAndRole = getEmailAndRoleFromToken(token);
                String email = emailAndRole[0];
                String role = emailAndRole[1];
                User user = userService.GetUserById(userId).get();
                if(Integer.parseInt(role) != 1 ){
                    if(!email.equals(user.getEmail())){
                        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Cannot access!"));
                    }
                }
                userService.deleteUserById(userId);
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseMessage("User deleted."));
            } catch (DatabaseErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
            catch (UserNotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

        @DeleteMapping("")
        public ResponseEntity<?> DeleteUserByEmail(@RequestBody GetUserByEmailDTO request,@RequestHeader("Authorization") String authorizationHeader){
            try {
                String token = authorizationHeader.substring(7);
                String[] emailAndRole = getEmailAndRoleFromToken(token);
                String email = emailAndRole[0];
                String role = emailAndRole[1];
                User user = userService.GetUserByEmail(request.getEmail()).get();

                if(Integer.parseInt(role) != 1 ){
                    if(!email.equals(user.getEmail())){
                        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Cannot access!"));
                    }
                }
                userService.deleteUserByEmail(request.getEmail());
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseMessage("User deleted."));
            } catch (DatabaseErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
            catch (UserNotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            catch (Exception e){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        @PutMapping(path = "{userId}")
        public ResponseEntity<?> UpdateUser(@PathVariable("userId") Long userId,@RequestBody UpdateUserDTO request, @RequestHeader("Authorization") String authorizationHeader) {
            try {
                String token = authorizationHeader.substring(7);
                String[] emailAndRole = getEmailAndRoleFromToken(token);
                String email = emailAndRole[0];
                String role = emailAndRole[1];
                User user = userService.GetUserById(userId).get();
                if(Integer.parseInt(role) != 1 ){
                    if(!email.equals(user.getEmail())){
                        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Cannot access!"));
                    }
                }
                User savedUser = userService.UpdateUser(userId, request.getFirstName(),request.getLastName(),request.getEmail(),request.getPassword(), request.getRole());
                return ResponseEntity.status(HttpStatus.OK).body(savedUser);
            } catch (UserExistsException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email taken");
            } catch (DatabaseErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }
