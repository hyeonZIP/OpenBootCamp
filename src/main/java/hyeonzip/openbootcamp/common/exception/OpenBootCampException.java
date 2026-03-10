package hyeonzip.openbootcamp.common.exception;

import lombok.Getter;

@Getter
public class OpenBootCampException extends RuntimeException {

    private final ErrorCode errorCode;

    public OpenBootCampException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public OpenBootCampException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
