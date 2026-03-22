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
<<<<<<< HEAD
    <T> Optional<T> findByEmail(String email);
=======

    boolean existsByEmailIgnoreCase(String email);
>>>>>>> f9bc24b276853b7295af4618fab93ac22a7d2719
}
