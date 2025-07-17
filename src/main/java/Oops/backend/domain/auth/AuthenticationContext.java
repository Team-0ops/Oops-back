package Oops.backend.domain.auth;

import Oops.backend.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
public class AuthenticationContext {
    private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    public void setPrincipal(User user) {
        userHolder.set(user);
    }

    public User getPrincipal() {
        return userHolder.get();
    }

    public void clear() {
        userHolder.remove();
    }
}