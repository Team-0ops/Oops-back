package Oops.backend.domain.auth;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final AuthenticationContext authenticationContext;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        AuthenticatedUser ann = parameter.getParameterAnnotation(AuthenticatedUser.class);
        boolean required = (ann == null) || ann.required();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            Object principal = auth.getPrincipal();
            if (principal instanceof User user) {
                return user;
            }
        }

        Object principal2 = authenticationContext.getPrincipal();
        if (principal2 instanceof User user2) {
            return user2;
        }

        if (required) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return null;
    }


}
