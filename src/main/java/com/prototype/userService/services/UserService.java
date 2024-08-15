package com.prototype.userService.services;

import com.prototype.userService.Pojos.UserResponseDTO;
import com.prototype.userService.models.entities.User;
import com.prototype.userService.models.nodes.AddressNode;
import com.prototype.userService.models.nodes.UserNode;
import com.prototype.userService.producer.KafkaSender;
import com.prototype.userService.repositories.AddressNodeRepository;
import com.prototype.userService.repositories.UserNodeRepository;
import com.prototype.userService.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressNodeRepository addressNodeRepository;
    private final UserNodeRepository userNodeRepository;
    private final RedisService redisService;
    private final KafkaSender kafkaSender;

    public UserService(UserRepository userRepository, AddressNodeRepository addressNodeRepository, KafkaSender kafkaSender,
                       UserNodeRepository userNodeRepository, RedisService redisService){
        this.userRepository=userRepository;
        this.addressNodeRepository=addressNodeRepository;
        this.userNodeRepository=userNodeRepository;
        this.redisService=redisService;
        this.kafkaSender=kafkaSender;
    }

    @Async
    public void getUserById(Long id, CompletableFuture<ResponseEntity<?>> completableFuture) {
        UserResponseDTO userResponseDTO=redisService.get("user"+id, UserResponseDTO.class);
        if (userResponseDTO == null) {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("UserNode not found"));
            userResponseDTO = new UserResponseDTO(user);
            redisService.set("user"+id, userResponseDTO, 300L);
        }
        completableFuture.complete(ResponseEntity.ok(userResponseDTO));
    }

    @Async
    public void getAllUsers(CompletableFuture<ResponseEntity<?>> completableFuture){
        List<UserResponseDTO> userResponseDTOS=redisService.get("users", List.class);
        if (userResponseDTOS == null) {
            List<User> users = userRepository.findAll();
            userResponseDTOS = users.stream()
                    .map(UserResponseDTO::new)
                    .collect(Collectors.toList());
            redisService.set("users", userResponseDTOS, 300L);
        }
        completableFuture.complete(ResponseEntity.ok(userResponseDTOS));
    }

    @Async
    public void getUsersByCity(String city, CompletableFuture<ResponseEntity<?>> completableFuture){
        List<UserResponseDTO> userResponseDTOS=redisService.get("users_of_"+city, List.class);
        if(userResponseDTOS == null) {
            List<UserNode> userNodes = addressNodeRepository.findUserNodesByCity(city);
            userResponseDTOS = userNodes.stream()
                    .map(UserResponseDTO::new)
                    .toList();
            redisService.set("users_of_"+city, userResponseDTOS, 300L);
        }
        completableFuture.complete(ResponseEntity.ok(userResponseDTOS));
    }

    @Async
    @Transactional
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
        UserResponseDTO userResponseDTO=new UserResponseDTO(user);
        kafkaSender.sendMessage(userResponseDTO.toString(), "user_created");
        completableFuture.complete(ResponseEntity.ok(userResponseDTO));
    }

    @Async
    @Transactional
    public void deleteUser(Long id) {
        redisService.getAndDelete("user"+id, UserResponseDTO.class);
        userRepository.deleteById(id);
    }
}
