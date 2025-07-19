package Oops.backend.domain.randomTopic.Repository;

import Oops.backend.domain.randomTopic.entity.RandomTopic;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RandomTopicRepository extends JpaRepository<RandomTopic, Long> {
}
