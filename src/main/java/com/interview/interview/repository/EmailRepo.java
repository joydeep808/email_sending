package com.interview.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interview.interview.entity.EmailEntity;

@Repository
public interface EmailRepo extends JpaRepository<EmailEntity, Long> {

}
