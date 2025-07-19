package Oops.backend.domain.user.entity;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_and_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAndCategory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
