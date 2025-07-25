package Oops.backend.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum ErrorStatus {

    // 공통 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "페이지를 찾을 수 없습니다."),

    TEMP_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "TEMP401", "인증 실패"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TEMP402", "토큰 오류"),

    // 입력값 검증 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALID401", "입력값이 올바르지 않습니다."),

    // 검색 관련 에러
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "SEARCH400", "검색어가 비어있을 수 없습니다."),
    SEARCH_RESULT_NOT_FOUND(HttpStatus.BAD_REQUEST, "SEARCH404", "해당 검색어와 일치하는 결과가 없습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "해당 사용자를 찾을 수 없습니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "해당 사용자에게 포인트가 존재하지 않습니다. (point == null)"),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "USER400", "해당 사용자의 포인트는 150점 이하입니다."),

    // 랜덤 주제 관련 에러
    INVALID_TOPIC_ID(HttpStatus.BAD_REQUEST, "TOPIC400", "해당 랜덤 주제 ID는 존재하지 않습니다. 1 ~ 20 범위 내의 ID를 요청해주세요. "),
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "TOPIC404", "해당 ID의 랜덤 주제가 존재하지 않습니다. "),

    // 게시물 관련 에러
    NO_POST(HttpStatus.NOT_FOUND, "POST404", "게시물이 존재하지 않습니다."),

    // 행운부적 관련 에러
    NO_LUCKY_DRAW(HttpStatus.INTERNAL_SERVER_ERROR, "LUCKYDRAW500", "행운부적이 존재하지 않습니다."),

    // 카테고리 관련 에러
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404", "해당 카테고리를 찾을 수 없습니다."),
    INVALID_CATEGORY_ID(HttpStatus.BAD_REQUEST, "CATEGORY400", "해당 카테고리 ID는 존재하지 않습니다. 1 ~ 15 범위 내의 ID를 요청해주세요. "),
    ALREADY_FAVORITE_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY400", "이미 즐겨찾기한 카테고리입니다."),
    NO_FAVORITE_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY400", "즐겨찾기 되어있지 않은 카테고리입니다."),
    NO_BOOKMARKED(HttpStatus.BAD_REQUEST, "CATEGORY400", "해당 사용자가 즐겨찾기한 카테고리가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }
}