package com.baesullin.pro.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkRequestDto {
    private int storeId;
    private int folderId;
}
