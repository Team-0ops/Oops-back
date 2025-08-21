package Oops.backend.domain.postGroup.repository;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.postGroup.entity.PostGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostGroupRepository extends JpaRepository<PostGroup, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PostGroup g
           set g.category = :free
         where g.category is null
           and exists (
                 select 1
                   from Post p
                  where p.postGroup = g
                    and p.topic.id = :topicId
           )
    """)
    int setGroupCategoryIfNullByTopic(@Param("topicId") Long topicId,
                                      @Param("free") Category free);
}
