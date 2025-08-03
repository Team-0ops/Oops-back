package Oops.backend.domain.commentReport.repository;

import Oops.backend.domain.commentReport.entity.CommentReport;
import Oops.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    long countByReportUser(User user);
}