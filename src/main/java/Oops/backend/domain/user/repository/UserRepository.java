package Oops.backend.domain.user.repository;

import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "lastLuckyDraws")
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findWithlastLuckyDrawsById(@Param("userId") Long userId);

    <T> Optional<T> findByEmail(String email);
}
