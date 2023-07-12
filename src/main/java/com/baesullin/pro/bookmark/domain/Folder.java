package com.baesullin.pro.bookmark.domain;


import com.baesullin.pro.user.domain.User;
import com.baesullin.pro.util.TimeStamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable = false)
    private User userId;

    @Column(nullable = false)
    //@Length(max = 15, message = "15자 이하로 입력해 주세요")
    //@NotBlank(message = "빈 칸을 입력하지 마세요")
    private String folderName;

    @JsonIgnore
    @OneToMany(mappedBy = "folderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();
}
