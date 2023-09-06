package com.baesullin.pro.bookmark.repository;

import com.baesullin.pro.bookmark.domain.Folder;
import com.baesullin.pro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findAllByUserId(User userId);
}
