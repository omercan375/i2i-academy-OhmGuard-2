package org.example.i2iacademyohmguard2.ai_recommendations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;

public interface AiRecommendationsRepo extends JpaRepository<AiRecommendationsTable, UUID> {



    List<AiRecommendationsTable> findByHomeIdOrderByGeneratedAtDesc(UUID homeId);
}
