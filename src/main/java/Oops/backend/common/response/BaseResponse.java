package Oops.backend.common.response;

import Oops.backend.common.status.ErrorStatus;
import Oops.backend.common.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "pageInfo", "result"})
public class BaseResponse {

    private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final PageInfo pageInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Object result;

    // 성공 응답 (전체)
    public static ResponseEntity<BaseResponse> onSuccess(SuccessStatus status, PageInfo pageInfo, Object result) {
        return new ResponseEntity<>(
                new BaseResponse(true, status.getCode(), status.getMessage(), pageInfo, result),
                status.getStatus()
        );
    }

    // 성공 - 데이터 없음
    public static ResponseEntity<BaseResponse> onSuccess(SuccessStatus status) {
        return onSuccess(status, null, null);
    }

    // 성공 - 데이터만
    public static ResponseEntity<BaseResponse> onSuccess(SuccessStatus status, Object result) {
        return onSuccess(status, null, result);
    }

    // 성공 - 페이징 응답
    public static ResponseEntity<BaseResponse> onSuccess(SuccessStatus status, Page<?> page) {
        PageInfo pageInfo = new PageInfo(page.getNumber(), page.getSize(), page.hasNext(), page.getTotalElements(), page.getTotalPages());
        return onSuccess(status, pageInfo, page.getContent());
    }

    // 실패 응답 (기본)
    public static ResponseEntity<BaseResponse> onFailure(ErrorStatus error) {
        return new ResponseEntity<>(
                new BaseResponse(false, error.getCode(), error.getMessage(), null, null),
                error.getStatus()
        );
    }

    // 실패 응답 (메시지 커스텀)
    public static ResponseEntity<BaseResponse> onFailure(ErrorStatus error, String message) {
        return new ResponseEntity<>(
                new BaseResponse(false, error.getCode(), error.getMessage(message), null, null),
                error.getStatus()
        );
    }
}
