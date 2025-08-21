package Oops.backend.domain.randomTopic.Service;

import Oops.backend.domain.randomTopic.dto.RandomTopicResponse;
import Oops.backend.domain.user.entity.User;

public interface RandomTopicService {
    RandomTopicResponse.BannarsInfoDto getBannarInfoAuth(User user);
    RandomTopicResponse.BannarsInfoDto getBannarInfoGuest();
}
