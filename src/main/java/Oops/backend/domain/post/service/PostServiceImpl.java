package Oops.backend.domain.post.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.domain.post.category.entity.Category;
import Oops.backend.domain.post.category.repository.CategoryRepository;
import Oops.backend.domain.post.dto.PostRequestDTO;
import Oops.backend.domain.post.entity.Post;
import Oops.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Long createPost(PostRequestDTO request, Long userId) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new GeneralException("해당 카테고리를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .situation(request.getSituation())
                .userId(userId)
                .category(category)
                .topicId(request.getTopicId())
                .previousPostId(request.getPreviousPostId())
                .allowedCommentTypes(request.getAllowedCommentTypes())
                .likes(0)
                .watching(0)
                .report(0)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);

        return post.getPostId();
    }
}
