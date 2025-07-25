package Oops.backend.config.s3;

import Oops.backend.common.status.ErrorStatus;
import lombok.Getter;

@Getter
public class S3Exception extends RuntimeException {
    private final ErrorStatus errorCode;

    public S3Exception(ErrorStatus errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}