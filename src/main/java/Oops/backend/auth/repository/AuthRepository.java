package Oops.backend.auth.repository;

import Oops.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
