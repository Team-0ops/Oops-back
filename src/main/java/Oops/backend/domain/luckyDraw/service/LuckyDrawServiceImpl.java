package Oops.backend.domain.luckyDraw.service;

import Oops.backend.common.exception.GeneralException;
import Oops.backend.common.status.ErrorStatus;
import Oops.backend.domain.luckyDraw.dto.LuckyDrawResponse;
import Oops.backend.domain.luckyDraw.entity.LuckyDraw;
import Oops.backend.domain.luckyDraw.repository.LuckyDrawRepository;
import Oops.backend.domain.user.entity.User;
import Oops.backend.domain.user.entity.UserLastLuckyDraw;
import Oops.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LuckyDrawServiceImpl implements LuckyDrawService {

    private final LuckyDrawRepository luckyDrawRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LuckyDrawResponse.LuckyDrawResponseDto getLuckyDraw(User user){
        User loadedUser = userRepository.findWithlastLuckyDrawsById(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Integer point = loadedUser.getPoint();

        if (point == null) {
            throw new GeneralException(ErrorStatus.POINT_NOT_FOUND);
        }
        if (point < 150){
            throw new GeneralException(ErrorStatus.NOT_ENOUGH_POINT);
        }

        // 기존에 뽑은 부적 목록 가져오기
        List<UserLastLuckyDraw> userDraws = loadedUser.getLastLuckyDraws();

        // LuckyDraw만 추출 (비교를 위해)
        List<LuckyDraw> lastLuckyDraws = userDraws.stream()
                .map(UserLastLuckyDraw::getLuckyDraw)
                .collect(Collectors.toList());

        LuckyDraw luckyDraw;
        if (lastLuckyDraws.isEmpty()){  // 처음 뽑는 경우
            luckyDraw = luckyDrawRepository
                    .findRandomLuckyDraw(PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.NO_LUCKY_DRAW));
        }else {
            luckyDraw = luckyDrawRepository
                    .findRandomExcludingList(lastLuckyDraws, PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.NO_LUCKY_DRAW));

        }

        // 기존 3개 유지 로직
        if (userDraws.size() >= 3) {
            UserLastLuckyDraw oldest = userDraws.get(0);
            userDraws.remove(0);
            loadedUser.getLastLuckyDraws().remove(oldest); // 양방향 관계일 경우
        }

        // 새로운 UserLastLuckyDraw 객체 생성 및 추가
        UserLastLuckyDraw newDraw = UserLastLuckyDraw.builder()
                .user(loadedUser)
                .luckyDraw(luckyDraw)
                .build();

        loadedUser.getLastLuckyDraws().add(newDraw);

        // 포인트 차감 및 저장
        loadedUser.setPoint(point - 150);
        userRepository.save(loadedUser);


        return LuckyDrawResponse.LuckyDrawResponseDto.from(luckyDraw);

    }
}

