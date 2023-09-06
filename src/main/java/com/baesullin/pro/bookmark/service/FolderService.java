package com.baesullin.pro.bookmark.service;

import com.baesullin.pro.bookmark.domain.Folder;
import com.baesullin.pro.bookmark.dto.FolderRequestDto;
import com.baesullin.pro.bookmark.dto.FolderResponseDto;
import com.baesullin.pro.bookmark.repository.BookmarkRepository;
import com.baesullin.pro.bookmark.repository.FolderRepository;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.store.repository.StoreRepository;
import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final StoreRepository storeRepository;

    /**
     * 폴더 생성
     */
    @Transactional
    public void folder(FolderRequestDto folderRequestDto, String socialId) {
        String folderName = folderRequestDto.getFolderName();
        if (folderName == null || folderName.equals("")) {
            throw new CustomException(ErrorCode.NULL_POINTER_EXCEPTION);
        }
        User user = userRepository.findBySocialId(socialId);
        Folder folder = Folder.builder()
                .folderName(folderRequestDto.getFolderName())
                .userId(user)
                .build();
        folderRepository.save(folder);
    }

    /**
     * 폴더 삭제
     */
    public void folderDelete(int folderId) {
        folderRepository.deleteById(folderId);
    }

    /**
     * 폴더 수정
     */
    public void folderUpdate(int folderId, String newFolderName) {
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new CustomException(ErrorCode.NO_FOLDER_FOUND));
        folder.setFolderName(newFolderName);
        folderRepository.save(folder);
    }


    /**
     * 폴더 조회
     */
    @Transactional(readOnly = true)
    public List<FolderResponseDto> folderList(String socialId) {
        User user = userRepository.findBySocialId(socialId);
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        List<FolderResponseDto> folderResponseDtoList = new ArrayList<>();
        for (Folder obj : user.getFolderList()) {
            FolderResponseDto folderResponseDto = FolderResponseDto.FolderDtoRes(obj);
            folderResponseDtoList.add(folderResponseDto);
        }
        return folderResponseDtoList;
    }
}
