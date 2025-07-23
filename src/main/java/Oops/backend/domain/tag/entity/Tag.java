package Oops.backend.domain.tag.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Builder
    private Tag(String name){
        this.name = name;
    }

    public static Tag of(String name){
        return Tag.builder().name(name).build();
    }

}
