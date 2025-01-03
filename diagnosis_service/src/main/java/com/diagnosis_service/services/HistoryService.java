package com.diagnosis_service.services;

import com.diagnosis_service.entity.History;
import com.diagnosis_service.repository.HistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Async
    public void storeQueryHistory(Long userId, String description, String result) {
        Optional<History> existingHistoryOpt = historyRepository.findTopByUserIdOrderByLastUpdatedDesc(userId);
        LocalDateTime now = LocalDateTime.now();

        if (existingHistoryOpt.isPresent()) {
            History existingHistory = existingHistoryOpt.get();

            if (existingHistory.getLastUpdated().isAfter(now.minusHours(24))) {
                existingHistory.getQueries().add("Description: " + description);
                existingHistory.getQueryResult().add("Result: " + result);
                existingHistory.setLastUpdated(now);

                historyRepository.save(existingHistory);
                return;
            }
        }

        History newHistory = new History();
        newHistory.setUserId(userId);
        newHistory.setQueries(new ArrayList<>(List.of("Description: " + description)));
        newHistory.setQueryResult(new ArrayList<>(List.of("Result: "+ result)));
        newHistory.setTimestamp(now);
        newHistory.setLastUpdated(now);

        historyRepository.save(newHistory);
    }

    public List<History> getRecentHistory(Long userId, int limit) {
        return historyRepository.findByUserIdOrderByTimestampDesc(userId, PageRequest.of(0, limit)).getContent();
    }
}
