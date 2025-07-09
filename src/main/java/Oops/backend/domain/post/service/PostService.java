package Oops.backend.domain.post.service;

import Oops.backend.domain.post.dto.PostRequestDTO;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Long createPost(PostRequestDTO request, Long userId);
}
