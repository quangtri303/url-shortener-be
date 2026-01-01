package com.tqt.shawttee.repository;

import com.tqt.shawttee.entity.Url;
import com.tqt.shawttee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    List<Url> findByOwner(User owner);
}
