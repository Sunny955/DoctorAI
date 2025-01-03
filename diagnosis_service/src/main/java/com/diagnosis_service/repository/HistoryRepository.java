package com.diagnosis_service.repository;

import com.diagnosis_service.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends MongoRepository<History, String> {
    List<History> findTop5ByUserIdOrderByTimestampDesc(String userId);
    Optional<History> findTopByUserIdOrderByLastUpdatedDesc(Long userId);
    Page<History> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
}
