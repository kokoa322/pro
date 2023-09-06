package com.baesullin.pro.admin.dto;

import com.baesullin.pro.store.domain.UserRegisterStore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminResponseDto {
    private long registerStoreId;
    private String name;
    private String address;
    private String elevator;
    private String toilet;
    private String heightDifferent;
    private String approach;

    @Builder
    public AdminResponseDto(
            UserRegisterStore userRegisterStore
    ) {
        this.registerStoreId = userRegisterStore.getId();
        this.name = userRegisterStore.getName();
        this.address = userRegisterStore.getAddress();
        this.elevator = userRegisterStore.getElevator();
        this.toilet = userRegisterStore.getToilet();
        this.heightDifferent = userRegisterStore.getHeightDifferent();
        this.approach = userRegisterStore.getApproach();
    }
}
