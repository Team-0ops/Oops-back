package Oops.backend.domain.failwiki.entity;

import Oops.backend.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FailWikiSummary extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String keyword; //검색

    @Column(columnDefinition = "TEXT")
    private String summary; // ai 요약 결과

    @Column(columnDefinition = "TEXT")
    private String aiTip; // 글 30개 안되면 ai 한마디

    @Column(nullable = false)
    private Integer postCount; // 요약 당시 게시글 개수
}