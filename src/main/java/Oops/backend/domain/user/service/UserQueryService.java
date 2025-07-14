package Oops.backend.domain.user.service;

import Oops.backend.domain.user.entity.User;

public interface UserQueryService {

    User findUser(Long userId);

}
