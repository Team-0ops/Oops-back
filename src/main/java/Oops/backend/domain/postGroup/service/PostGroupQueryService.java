package Oops.backend.domain.postGroup.service;

import Oops.backend.domain.postGroup.dto.GetPostGroupResponse;
import Oops.backend.domain.user.entity.User;

public interface PostGroupQueryService {

    GetPostGroupResponse getPostGroup(User user, Long postId);

}
