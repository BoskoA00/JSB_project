package com.example.demo.Repositories;

import com.example.demo.Models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdsRepository extends JpaRepository<Ad,Long> {


    @Query("SELECT a FROM Ad a WHERE a.id = ?1")
    Optional<Ad> getAdById(Long adId);
    @Query("SELECT a FROM Ad a WHERE a.user.id = ?1")
    List<Ad> getAdsByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ad a WHERE a.user.id =?1")
    void deleteByUserId(Long userId);

}
