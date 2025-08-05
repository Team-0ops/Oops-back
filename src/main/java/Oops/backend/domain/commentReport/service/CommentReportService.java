package Oops.backend.domain.commentReport.service;

import Oops.backend.domain.comment.entity.Comment;
import Oops.backend.domain.comment.service.CommentQueryService;
import Oops.backend.domain.commentReport.dto.CommentReportRequest;
import Oops.backend.domain.commentReport.entity.CommentReport;
import Oops.backend.domain.commentReport.repository.CommentReportRepository;
import Oops.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentQueryService commentQueryService;
    private final CommentReportRepository commentReportRepository;

    @Transactional
    public void reportComment(Long commentId, User user, CommentReportRequest request){

        Comment comment = commentQueryService.findComment(commentId);

        CommentReport newCommentReport = CommentReport.of(user, comment, request.getContent());

        commentReportRepository.save(newCommentReport);
    }


}
