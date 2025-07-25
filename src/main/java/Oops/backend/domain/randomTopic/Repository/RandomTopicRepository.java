package Oops.backend.domain.randomTopic.Repository;

import Oops.backend.domain.randomTopic.entity.RandomTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface RandomTopicRepository extends JpaRepository<RandomTopic, Long> {
    @Query("SELECT r.name FROM RandomTopic r WHERE r.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
}
