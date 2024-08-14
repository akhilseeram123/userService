package com.prototype.userService.Pojos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String userName;
    private String password;
    private String address;
}
