package Oops.backend.domain.post.controller;

import Oops.backend.domain.post.dto.PostRequestDTO;
import Oops.backend.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestBody @Valid PostRequestDTO request,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        Long postId = postService.createPost(request, userId);
        return ResponseEntity.ok(postId);
    }
}