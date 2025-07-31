package Oops.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Oops - 실패담 공유 플랫폼 API")
                        .description("""
                        😅 **웁스(oops)**는 실패 경험을 나누고 공감하며 함께 성장하는 웹 플랫폼입니다.

                        🙌 누구나 실수할 수 있지만, 그 경험은 누군가에겐 큰 인사이트가 됩니다.
                        
                        📌 주요 기능:
                        - 회원가입 및 로그인
                        - 실패담 게시글 작성 및 열람
                        - 교훈(Lesson) 정리 및 공유
                        - 댓글 및 대댓글을 통한 소통
                        - 카테고리 기반 관심사 설정
                        - 태그, 스크랩, 포인트 시스템 등

                        🔐 일부 API는 인증이 필요하며, JWT를 통해 보호됩니다.
                       
                        """)
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("jwtAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("jwtAuth",
                                new SecurityScheme()
                                        .name("jwtAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
