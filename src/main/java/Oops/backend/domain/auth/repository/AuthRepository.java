package Oops.backend.domain.auth.repository;

import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
