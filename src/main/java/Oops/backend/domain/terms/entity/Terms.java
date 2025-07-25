package Oops.backend.domain.terms.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Terms extends BaseEntity {

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String required;
}