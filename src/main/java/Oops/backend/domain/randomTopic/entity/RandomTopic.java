package Oops.backend.domain.randomTopic.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RandomTopic extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long nextTopicId;

    @Column(nullable = false)
    private String image;
}
