package com.prototype.userService.controllers;

import com.prototype.userService.Pojos.CreateUserRequest;
import com.prototype.userService.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<?>> getUserById(@PathVariable Long id) {
        DeferredResult<ResponseEntity<?>> deferredResult =new DeferredResult<>();
        CompletableFuture<ResponseEntity<?>> completableFuture=new CompletableFuture<>();
        userService.getUserById(id, completableFuture);
        completableFuture.thenAccept(deferredResult::setResult);
        return deferredResult;
    }

    @GetMapping()
    public DeferredResult<ResponseEntity<?>> getAllUsers(){
        DeferredResult<ResponseEntity<?>> deferredResult =new DeferredResult<>();
        CompletableFuture<ResponseEntity<?>> completableFuture=new CompletableFuture<>();
        userService.getAllUsers(completableFuture);
        completableFuture.thenAccept(deferredResult::setResult);
        return deferredResult;
    }

    @GetMapping("/{city}/city")
    public DeferredResult<ResponseEntity<?>> getUsersByCity(@PathVariable String city){
        DeferredResult<ResponseEntity<?>> deferredResult =new DeferredResult<>();
        CompletableFuture<ResponseEntity<?>> completableFuture=new CompletableFuture<>();
        userService.getUsersByCity(city, completableFuture);
        completableFuture.thenAccept(deferredResult::setResult);
        return deferredResult;
    }


    @PostMapping
    public DeferredResult<ResponseEntity<?>> createUser(@RequestBody CreateUserRequest createUserRequest) {
        DeferredResult<ResponseEntity<?>> deferredResult =new DeferredResult<>();
        CompletableFuture<ResponseEntity<?>> completableFuture=new CompletableFuture<>();
        userService.saveUser(createUserRequest.getUserName(), createUserRequest.getPassword(), createUserRequest.getCity(), completableFuture);
        completableFuture.thenAccept(deferredResult::setResult);
        return deferredResult;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
