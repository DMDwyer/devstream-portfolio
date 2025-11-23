package com.dmdwyer.devstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmdwyer.devstream.entity.Flag;

import java.util.Optional;

public interface FlagRepository extends JpaRepository<Flag, Long> {
  Optional<Flag> findByFlagKey(String flagKey);
  boolean existsByFlagKey(String flagKey);
}
