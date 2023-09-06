package com.baesullin.pro.store.dto;

import com.baesullin.pro.store.domain.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StorePagedResponseDto {
    private boolean hasNextPage;
    private long totalCount;
    private long leftElement;
    private int page;
    private int totalPage;
    private List<StoreCardResponseDto> cards;

    public StorePagedResponseDto(Page<Store> resultStoreList, List<StoreCardResponseDto> cards) {
        this.hasNextPage = resultStoreList.hasNext();
        this.totalPage = resultStoreList.getTotalPages() - 1;
        this.cards = cards;
        this.totalCount = resultStoreList.getTotalElements();
        this.page = resultStoreList.getNumber();
        this.leftElement = totalCount - (long) page * resultStoreList.getSize() - resultStoreList.getNumberOfElements();
        this.leftElement = this.leftElement < 0 ? 0 : this.leftElement;
    }
}