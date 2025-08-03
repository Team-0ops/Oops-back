package Oops.backend.domain.randomTopic.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RandomTopic extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_random_id")
    private RandomTopic nextRandomTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_random_id")
    private RandomTopic lastRandomTopic;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Integer isCurrent = 0;
}
