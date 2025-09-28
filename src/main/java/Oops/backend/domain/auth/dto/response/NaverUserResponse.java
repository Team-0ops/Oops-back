package Oops.backend.domain.auth.dto.response;
import lombok.Getter;

@Getter
public class NaverUserResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    public static class Response {
        private String id;
        private String email;
        private String name;
        private String profile_image;
    }
}