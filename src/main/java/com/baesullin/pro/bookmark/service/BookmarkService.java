package com.baesullin.pro.bookmark.service;

import com.baesullin.pro.bookmark.domain.Bookmark;
import com.baesullin.pro.bookmark.domain.Folder;
import com.baesullin.pro.bookmark.dto.BookmarkInfoDto;
import com.baesullin.pro.bookmark.dto.BookmarkPagedResponseDto;
import com.baesullin.pro.bookmark.dto.BookmarkRequestDto;
import com.baesullin.pro.bookmark.repository.BookmarkRepository;
import com.baesullin.pro.bookmark.repository.FolderRepository;
import com.baesullin.pro.exception.CustomException;
import com.baesullin.pro.exception.ErrorCode;
import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.store.repository.StoreRepository;
import com.baesullin.pro.store.service.StoreService;
import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FolderRepository folderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreService storeService;

    @Transactional
    public void bookmark(BookmarkRequestDto bookmarkRequestDto, String socialId) {
        // 북마크 폴더 생성하는 Flow를 따르므로 bookmarkRequestDto의 folderId는 존재
        Folder folder = folderRepository.findById(bookmarkRequestDto.getFolderId()).orElseThrow(() -> new CustomException(ErrorCode.NO_FOLDER_FOUND));
        Store store = storeRepository.findById((long) bookmarkRequestDto.getStoreId()).orElseThrow(() -> new CustomException(ErrorCode.NO_BOOKMARK_FOUND));
        User user = userRepository.findBySocialId(socialId);
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }

        Bookmark bookmark = Bookmark
                .builder()
                .folderId(folder)
                .storeId(store)
                .userId(user)
                .build();

        if (!bookmarkRepository.existsByStoreIdAndUserId(store, user)) {
            bookmarkRepository.save(bookmark);
            storeService.updateBookmarkCnt(store, socialId);
        }
    }

    @Transactional
    public void bookmarkDelete(Long storeId, String socialId) {
        User user = userRepository.findBySocialId(socialId);
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new CustomException(ErrorCode.NO_STORE_FOUND));
        Bookmark bookmark = bookmarkRepository.findByStoreIdAndUserId(store, user).orElseThrow(() -> new CustomException(ErrorCode.NO_BOOKMARK_FOUND));
        store.removeBookmark(bookmark);
        bookmarkRepository.delete(bookmark);
        storeService.updateBookmarkCnt(store, socialId);
    }


    @Transactional
    public List<BookmarkInfoDto> bookmarkTop(String socialId, Pageable pageable) {

        User user = userRepository.findBySocialId(socialId);
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByUserId(user, pageable);

        List<BookmarkInfoDto> bookmarkList = new ArrayList<>();
        for (Bookmark bookmark : bookmarkPage) {
            BookmarkInfoDto bookmarkInfoDto = new BookmarkInfoDto(bookmark);
            bookmarkList.add(bookmarkInfoDto);
        }
        return bookmarkList;
    }

    @Transactional
    public BookmarkPagedResponseDto bookmarkList(String socialId, int folderId, Pageable pageable) {
        User user = userRepository.findBySocialId(socialId);
        if (user == null) {
            throw new CustomException(ErrorCode.NO_USER_FOUND);
        }
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new CustomException(ErrorCode.NO_FOLDER_FOUND));
        Page<Bookmark> pagedBookmark = bookmarkRepository.findAllByFolderId(folder, pageable);
        return new BookmarkPagedResponseDto(pagedBookmark);
    }
}
