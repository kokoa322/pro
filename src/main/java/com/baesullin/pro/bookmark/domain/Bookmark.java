package com.baesullin.pro.bookmark.domain;

import com.baesullin.pro.store.domain.Store;
import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.util.TimeStamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FOLDER_ID", nullable = false)
    private Folder folderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="STORE_ID", nullable = false)
    private Store storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable = false)
    private User userId;

}
