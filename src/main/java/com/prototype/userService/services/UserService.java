package com.prototype.userService.services;

import com.prototype.userService.Pojos.UserResponseDTO;
import com.prototype.userService.models.User;
import com.prototype.userService.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Cacheable(value = "users", key = "#id")
    @Async
    public void getUserById(Long id, CompletableFuture<ResponseEntity<?>> completableFuture) {
        User user= userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("User not found"));
        UserResponseDTO userResponseDTO=new UserResponseDTO(user);
        completableFuture.complete(ResponseEntity.ok(userResponseDTO));
    }

    @Cacheable("users")
    @Async
    public void getAllUsers(CompletableFuture<ResponseEntity<?>> completableFuture){
        List<User> users=userRepository.findAll();
        if(users==null){
            throw new EntityNotFoundException("user not found");
        }
        List<UserResponseDTO> userResponseDTOS = users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
        completableFuture.complete(ResponseEntity.ok(userResponseDTOS));
    }

    @CachePut(value = "users", key = "#user.id")
    @Async
    public void saveUser(String userName, String password, String address, CompletableFuture<ResponseEntity<?>> completableFuture ) {
        User user=User.builder()
                .userName(userName)
                .password(password)
                .address(address)
                .build();
        user=userRepository.save(user);
        completableFuture.complete(ResponseEntity.ok(user));
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
