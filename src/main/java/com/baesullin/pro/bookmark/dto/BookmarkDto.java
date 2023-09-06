package com.baesullin.pro.bookmark.dto;

import com.baesullin.pro.bookmark.domain.Folder;
import com.baesullin.pro.store.domain.Store;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkDto {

    private List<Store> storeList = new ArrayList<>();

    private List<Folder> folderList = new ArrayList<>();
}
