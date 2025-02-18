package com.interview.interview.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.interview.interview.entity.EmailEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface EmailRepo extends JpaRepository<EmailEntity, String> {
  @Query(value = "SELECT * from emails where recipient = :recipient order by created_at DESC", nativeQuery = true)
  List<EmailEntity> findByEmailId(@Param("recipient") String recipient);
}
