package Oops.backend.domain.postGroup.service;

import Oops.backend.domain.postGroup.entity.PostGroup;
import Oops.backend.domain.postGroup.repository.PostGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostGroupCommandService {

    private final PostGroupRepository postGroupRepository;

    public void deletePostGroup(PostGroup postGroup){

        postGroupRepository.delete(postGroup);

    }

}

//