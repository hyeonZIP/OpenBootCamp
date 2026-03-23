package hyeonzip.openbootcamp.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ── 공통 ─────────────────────────────────────────────────────
    INVALID_INPUT("유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // ── Bootcamp ─────────────────────────────────────────────────
    BOOTCAMP_NOT_FOUND("존재하지 않는 부트캠프입니다.", HttpStatus.NOT_FOUND),
    BOOTCAMP_SLUG_DUPLICATE("이미 사용 중인 부트캠프 슬러그입니다.", HttpStatus.CONFLICT),
    BOOTCAMP_NAME_DUPLICATE("이미 사용 중인 부트캠프 이름입니다.", HttpStatus.CONFLICT),

    // ── Project ──────────────────────────────────────────────────
    PROJECT_NOT_FOUND("존재하지 않는 프로젝트입니다.", HttpStatus.NOT_FOUND),
    PROJECT_UNAUTHORIZED("본인의 프로젝트만 수정/삭제할 수 있습니다.", HttpStatus.FORBIDDEN),

    // ── Auth ─────────────────────────────────────────────────────
    UNSUPPORTED_OAUTH2_PROVIDER("지원하지 않는 OAuth2 제공자입니다.", HttpStatus.BAD_REQUEST),

    // ── GitHub ───────────────────────────────────────────────────
    GITHUB_API_ERROR("GitHub API 호출 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    GITHUB_REPO_NOT_FOUND("GitHub 레포지토리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ── User ─────────────────────────────────────────────────────
    USER_NOT_FOUND("존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),

    // ── Review ───────────────────────────────────────────────────
    REVIEW_NOT_FOUND("존재하지 않는 리뷰입니다.", HttpStatus.NOT_FOUND),
    REVIEW_DUPLICATE("이미 해당 부트캠프에 리뷰를 작성했습니다.", HttpStatus.CONFLICT),
    REVIEW_UNAUTHORIZED("본인의 리뷰만 수정/삭제할 수 있습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
