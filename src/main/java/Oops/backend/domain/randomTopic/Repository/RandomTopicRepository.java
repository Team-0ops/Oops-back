package Oops.backend.domain.randomTopic.Repository;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.randomTopic.entity.RandomTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;


public interface RandomTopicRepository extends JpaRepository<RandomTopic, Long> {
    @Query("SELECT r.name FROM RandomTopic r WHERE r.id = :id")
    Optional<String> findNameById(@Param("id") Long id);

    @Query("SELECT r FROM RandomTopic r WHERE r.isCurrent = 1")
    Optional<RandomTopic> findCurrentTopic();

    @Modifying
    @Transactional
    @Query("UPDATE RandomTopic r SET r.isCurrent = 0")
    void resetAllCurrent();

    List<RandomTopic> findByNameContainingIgnoreCase(String name);
}
