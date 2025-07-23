package Oops.backend.domain.tag.repository;

import Oops.backend.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);

    boolean existsTagByName(String name);

}
