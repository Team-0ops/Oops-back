package Oops.backend.domain.failurewiki.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "failure_keyword")
public class FailureKeyword extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String keyword;

    @Column(nullable = false, length = 500)
    private String summaryTip;

    // 추후 연관관계가 생기면 여기에 추가
}