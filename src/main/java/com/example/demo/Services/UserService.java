package com.example.demo.Services;

import java.util.List;
import java.util.Optional;

import com.example.demo.DTOS.UpdateUserDTO;
import com.example.demo.Exceptions.UserExistsException;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.Models.User;
import com.example.demo.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService( UserRepository userRepository){
        this.userRepository = userRepository;
    }

      public List<User> getUsers() {
        return userRepository.findAll();
    }
    public User CreateUser(User user){

       Optional<User> userExists = userRepository.findUserByEmail(user.getEmail());
        if(userExists.isPresent()){
            throw new UserExistsException("User with this email already exists");
        }else{
            return userRepository.save(user);
        }
    }
    public Optional<User> GetUserById(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            return user;
        }else{
            throw new UserNotFoundException("User with this id doesn't exist");
        }
    }
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with this id doesn't exist");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            userRepository.deleteUserByEmail(email);
        } else {
            throw new UserNotFoundException("User with this email doesn't exist");
        }
    }

    public Optional<User> GetUserByEmail(String email){
        Optional<User> user = userRepository.findUserByEmail(email);
        if(user.isPresent()){
            return  user;
        }
        else{
            throw  new UserNotFoundException("User with this email doesn't exist");
        }
    }
    @Transactional
    public User UpdateUser(Long userId, String firstName, String lastName, String email, String password, Integer role) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with this id doesn't exist");
        }

        User user = userOptional.get();

        if (firstName != null && !firstName.trim().isEmpty()) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            user.setLastName(lastName);
        }
        if (email != null && !email.trim().isEmpty()) {
            Optional<User> existingUser = userRepository.findUserByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new UserExistsException("User with this email already exists");
            }
            user.setEmail(email);
        }
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password);
        }
        if (role != null) {
            user.setRole(role);
        }

        return userRepository.save(user);
    }


}
