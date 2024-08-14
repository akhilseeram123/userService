package com.prototype.userService.Pojos;

import com.prototype.userService.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    public UserResponseDTO(User user){
        this.userName=user.getUserName();
        this.address=user.getAddress();
    }

    private String userName;
    private String address;
}
