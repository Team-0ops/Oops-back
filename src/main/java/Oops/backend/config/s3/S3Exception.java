package capstone.mju.backend.global.s3;

import capstone.mju.backend.domain.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class S3Exception extends RuntimeException {
    private final ErrorCode errorCode;

    public S3Exception(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}