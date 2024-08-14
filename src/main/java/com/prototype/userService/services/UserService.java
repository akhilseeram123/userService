package com.prototype.userService.services;

import com.prototype.userService.Pojos.UserResponseDTO;
import com.prototype.userService.models.entities.User;
import com.prototype.userService.models.nodes.AddressNode;
import com.prototype.userService.models.nodes.UserNode;
import com.prototype.userService.repositories.AddressNodeRepository;
import com.prototype.userService.repositories.UserNodeRepository;
import com.prototype.userService.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressNodeRepository addressNodeRepository;
    private final UserNodeRepository userNodeRepository;

    public UserService(UserRepository userRepository, AddressNodeRepository addressNodeRepository, UserNodeRepository userNodeRepository){
        this.userRepository=userRepository;
        this.addressNodeRepository=addressNodeRepository;
        this.userNodeRepository=userNodeRepository;
    }

    @Cacheable(value = "users", key = "#id")
    @Async
    public void getUserById(Long id, CompletableFuture<ResponseEntity<?>> completableFuture) {
        User user= userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("UserNode not found"));
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

    @Cacheable("users")
    @Async
    public void getUsersByCity(String city, CompletableFuture<ResponseEntity<?>> completableFuture){
        List<UserNode> userNodes=addressNodeRepository.findUserNodesByCity(city);
        if(userNodes==null){
            throw new EntityNotFoundException("UserNode not found");
        }
        List<UserResponseDTO> userResponseDTOS = userNodes.stream()
                .map(UserResponseDTO::new)
                .toList();
        completableFuture.complete(ResponseEntity.ok(userResponseDTOS));
    }

    @CachePut(value = "users", key = "#user.id")
    @Async
    public void saveUser(String userName, String password, String city, CompletableFuture<ResponseEntity<?>> completableFuture ) {
        User user=User.builder()
                .userName(userName)
                .password(password)
                .city(city)
                .build();
        user=userRepository.save(user);
        AddressNode addressNode=AddressNode.builder()
                        .city(city)
                        .build();
        addressNode = addressNodeRepository.save(addressNode);
        UserNode userNode=UserNode.builder()
                        .userName(userName)
                        .addressNode(addressNode)
                        .build();
        userNodeRepository.save(userNode);
        completableFuture.complete(ResponseEntity.ok(user));
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
