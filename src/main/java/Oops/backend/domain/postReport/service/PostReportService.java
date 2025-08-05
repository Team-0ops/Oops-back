package Oops.backend.domain.postReport.service;

import Oops.backend.domain.post.dto.PostReportRequest;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.service.PostQueryService;
import Oops.backend.domain.postReport.entity.PostReport;
import Oops.backend.domain.postReport.repository.PostReportRepository;
import Oops.backend.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostReportService {

    private final PostQueryService postQueryService;
    private final PostReportRepository postReportRepository;

    @Transactional
    public void reportPost(Long postId, User user, PostReportRequest request){

        Post post = postQueryService.findPost(postId);

        PostReport newPostReport = PostReport.of(user, post, request.getContent());

        postReportRepository.save(newPostReport);
    }
}
