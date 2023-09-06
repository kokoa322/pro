package com.baesullin.pro.admin.service;

import com.baesullin.pro.admin.dto.AdminResponseDto;
import com.baesullin.pro.store.domain.UserRegisterStore;
import com.baesullin.pro.store.repository.UserRegisterStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRegisterStoreRepository userRegisterStoreRepository;

    /**
     * 유저가 등록한 업장 조회
     * @return
     */
    public List<AdminResponseDto> getUserRegisterStore() {
        Page<UserRegisterStore> foundUserRegisterStores = userRegisterStoreRepository.findAll(Pageable.ofSize(5));

        List<AdminResponseDto> result = new ArrayList<>();

        for (UserRegisterStore foundUserRegisterStore : foundUserRegisterStores.getContent()) {
            AdminResponseDto adminResponseDto = new AdminResponseDto(foundUserRegisterStore);
            result.add(adminResponseDto);
        }

        return result;
    }
}
