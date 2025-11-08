package com.dmdwyer.devstream.repository;

import com.dmdwyer.devstream.entity.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsightRepository extends JpaRepository<Insight, Long> {

}
