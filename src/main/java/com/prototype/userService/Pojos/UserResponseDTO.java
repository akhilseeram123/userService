package com.prototype.userService.Pojos;

import com.prototype.userService.models.entities.User;
import com.prototype.userService.models.nodes.UserNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {

    public UserResponseDTO(User user){
        this.userName=user.getUserName();
        this.city =user.getCity();
    }

    public UserResponseDTO(UserNode userNode){
        this.userName=userNode.getUserName();
        this.city=userNode.getAddressNode().getCity();
    }

    private String userName;
    private String city;
}
