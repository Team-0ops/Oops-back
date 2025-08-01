package Oops.backend.domain.postReport.repository;

import Oops.backend.domain.postReport.entity.PostReport;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    long countByUser(User user);
}
