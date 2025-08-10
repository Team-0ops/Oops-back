package Oops.backend.domain.failwiki.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FailWikiListItemDto {
    private String keyword;
    private String summary;   // 있으면 그대로
    private String aiTip;     // 없으면 null
    private Integer postCount;
    private LocalDateTime modifiedAt;
}

