package com.baesullin.pro.store.dto.userRegisterStore;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterStoreImgDto {
    private String userRegisterStoreImageUrl;

    public UserRegisterStoreImgDto(String userRegisterStoreImageUrl) {
        this.userRegisterStoreImageUrl = userRegisterStoreImageUrl;
    }
}
