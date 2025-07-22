package Oops.backend.domain.luckyDraw.service;

import Oops.backend.domain.luckyDraw.dto.LuckyDrawResponse;
import Oops.backend.domain.user.entity.User;

public interface LuckyDrawService {
    LuckyDrawResponse.LuckyDrawResponseDto getLuckyDraw(User user);
}
