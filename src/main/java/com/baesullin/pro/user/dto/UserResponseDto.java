package com.baesullin.pro.user.dto;


import com.baesullin.pro.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponseDto implements Serializable {
    private String name;
    private String email;
    private String userImage;

    public UserResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.userImage = user.getProfileImageUrl();
    }
}