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

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequiredType required;

    public RequiredType getRequired() {
        return required;
    }

    public void setRequired(RequiredType required) {
        this.required = required;
    }
}