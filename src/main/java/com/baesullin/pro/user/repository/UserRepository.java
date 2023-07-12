package com.baesullin.pro.user.repository;

import com.baesullin.pro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findBySocialId(String socialId);

    User findByEmail(String email);
}
