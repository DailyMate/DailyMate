package com.dailymate.domain.diary.service;

import com.dailymate.domain.alert.dao.AlertRepository;
import com.dailymate.domain.alert.dto.AlertReqDto;
import com.dailymate.domain.alert.service.AlertService;
import com.dailymate.domain.diary.constant.Feeling;
import com.dailymate.domain.diary.constant.OpenType;
import com.dailymate.domain.diary.constant.Weather;
import com.dailymate.domain.diary.dao.DiaryRepository;
import com.dailymate.domain.diary.dao.LikeDiaryRepository;
import com.dailymate.domain.diary.domain.Diary;
import com.dailymate.domain.diary.domain.LikeDiary;
import com.dailymate.domain.diary.domain.LikeDiaryKey;
import com.dailymate.domain.diary.dto.DiaryMonthlyResDto;
import com.dailymate.domain.diary.dto.DiaryReqDto;
import com.dailymate.domain.diary.dto.DiaryResDto;
import com.dailymate.domain.diary.exception.DiaryBadRequestException;
import com.dailymate.domain.diary.exception.DiaryExceptionMessage;
import com.dailymate.domain.diary.exception.DiaryForbiddenException;
import com.dailymate.domain.diary.exception.DiaryNotFoundException;
import com.dailymate.domain.friend.dao.FriendRepository;
import com.dailymate.domain.friend.domain.Friend;
import com.dailymate.domain.user.dao.UserRepository;
import com.dailymate.domain.user.domain.Users;
import com.dailymate.domain.user.exception.UserExceptionMessage;
import com.dailymate.domain.user.exception.UserNotFoundException;
import com.dailymate.global.common.jwt.JwtTokenProvider;
import com.dailymate.global.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageService imageService;
    private final LikeDiaryRepository likeDiaryRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FriendRepository friendRepository;
    private final AlertService alertService;

    /**
     * 일기 작성
     * @param accessToken String
     * @param diaryReqDto DiaryReqDto
     * @param image MultipartFile
     */
    @Override
    @Transactional
    public void addDiary(String accessToken, DiaryReqDto diaryReqDto, MultipartFile image) {

        // 제목 입력값 검증
        if(!StringUtils.hasText(diaryReqDto.getTitle()) || diaryReqDto.getTitle().length() > 105) {
            throw new DiaryBadRequestException("[ADD_DIARY] " + DiaryExceptionMessage.DIARY_BAD_REQUEST.getMsg());
        }

        // 사용자 존재하는지 확인
        Users user = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[ADD_DIARY] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        // 해당 날짜에 일기가 존재하는지 확인
        if(diaryRepository.existsDiaryByDateAndUsers(diaryReqDto.getDate(), user)) {
            throw new DiaryBadRequestException("[ADD_DIARY] " + DiaryExceptionMessage.DIARY_ALREADY_EXIST.getMsg());
        }

        Diary diary = Diary.createDiary(diaryReqDto, user);

        // 이미지 등록
        if(image != null && !image.isEmpty()) {
            String imageUrl = imageService.uploadImage(image);
            diary.updateImage(imageUrl);
        }

        diaryRepository.save(diary);
    }

    /**
     * 일기 수정
     * @param accessToken String
     * @param diaryReqDto DiaryReqDto
     * @param image MultipartFile
     */
    @Override
    @Transactional
    public void updateDiary(String accessToken, Long diaryId, DiaryReqDto diaryReqDto, MultipartFile image) {

        // 제목 입력값 검증
        if(!StringUtils.hasText(diaryReqDto.getTitle()) || diaryReqDto.getTitle().length() > 105) {
            throw new DiaryBadRequestException("[UPDATE_DIARY] " + DiaryExceptionMessage.DIARY_BAD_REQUEST.getMsg());
        }

        // 일기 존재하는 지 확인
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotFoundException("[UPDATE_DIARY] " + DiaryExceptionMessage.DIARY_NOT_FOUND.getMsg()));

        // 이미 삭제된 일기인지 확인
        if(diary.getDeletedAt() != null) {
            throw new DiaryNotFoundException("[UPDATE_DIARY] " + DiaryExceptionMessage.DIARY_ALREADY_DELETED.getMsg());
        }

        // 사용자 존재하는지 확인
        Users users = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[UPDATE_DIARY] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        // 일기 작성자와 같은지 확인
        if(diary.getUsers() != users) {
            throw new DiaryForbiddenException("[UPDATE_DIARY] " + DiaryExceptionMessage.DIARY_HANDLE_ACCESS_DENIED.getMsg());
        }

        // 일기 수정
        diary.updateDiary(diaryReqDto.getTitle(),
                diaryReqDto.getContent(),
                diaryReqDto.getDate(),
                Weather.getWeather(diaryReqDto.getWeather()),
                Feeling.getFeeling(diaryReqDto.getFeeling()),
                OpenType.getOpenType(diaryReqDto.getOpenType()));

        // 이미지 입력값 확인
        if(image != null && !image.isEmpty()) {

            // 기존 이미지가 존재한다면 삭제
            if(diary.getImage() != null) {
                imageService.deleteImage(diary.getImage());
            }

            // 새로운 이미지 등록
            String imageUrl = imageService.uploadImage(image);
            diary.updateImage(imageUrl);
        }

    }

    /**
     * 일기 삭제
     * @param accessToken String
     * @param diaryId Long
     */
    @Override
    @Transactional
    public void deleteDiary(String accessToken, Long diaryId) {

        // 일기 존재하는 지 확인
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotFoundException("[DELETE_DIARY] " + DiaryExceptionMessage.DIARY_NOT_FOUND.getMsg()));

        // 이미 삭제된 일기인지 확인
        if(diary.getDeletedAt() != null) {
            throw new DiaryNotFoundException("[DELETE_DIARY] " + DiaryExceptionMessage.DIARY_ALREADY_DELETED.getMsg());
        }

        // 사용자 존재하는지 확인
        Users users = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[DELETE_DIARY] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        // 일기 작성자와 같은지 확인
        if(diary.getUsers() != users) {
            throw new DiaryForbiddenException("[DELETE_DIARY] " + DiaryExceptionMessage.DIARY_HANDLE_ACCESS_DENIED.getMsg());
        }

        // 이미지가 존재한다면 삭제
        if(diary.getImage() != null) {
            imageService.deleteImage(diary.getImage());
        }

        // 좋아요 삭제
        likeDiaryRepository.deleteAllByDiary(diary);

        // 일기 삭제
        diary.delete();
    }

    /**
     * 일기 좋아요
     * @param accessToken String
     * @param diaryId Long
     */
    @Override
    @Transactional
    public void likeDiary(String accessToken, Long diaryId) {

        // 일기 확인
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotFoundException("[LIKE_DIARY] " + DiaryExceptionMessage.DIARY_NOT_FOUND.getMsg()));

        // 사용자 존재하는지 확인
        Users user = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[LIKE_DIARY] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        // 복합키 생성
        LikeDiaryKey key = LikeDiaryKey.createKey(user.getUserId(), diaryId);

        // 좋아요 토글
        Optional<LikeDiary> likeDiary = likeDiaryRepository.findById(key);

        if(likeDiary.isPresent()) {
            likeDiaryRepository.delete(likeDiary.get());
        } else {
            likeDiaryRepository.save(LikeDiary.builder()
                    .user(user)
                    .diary(diary)
                    .build());

            // 알림 전송
            AlertReqDto alert = new AlertReqDto(diary.getUsers().getUserId(), user.getUserId(), diaryId, "일기좋아요");
            alertService.addAlert(alert);
        }

    }

    /**
     * 일기 조회 (일별)
     * @param accessToken String
     * @param diaryId Long
     * @return DiaryResDto
     */
    @Override
    @Transactional
    public DiaryResDto findDiary(String accessToken, Long diaryId) {

        // 사용자 확인
        Users user = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[FIND_DIARY] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        // 일기 정보 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotFoundException("[FIND_DIARY] " + DiaryExceptionMessage.DIARY_NOT_FOUND.getMsg()));

        // 일기 확인
        if(diary == null) {
            throw new DiaryNotFoundException("[FIND_DIARY] " + DiaryExceptionMessage.DIARY_NOT_FOUND.getMsg());
        }

        // 좋아요 확인
        LikeDiaryKey key = LikeDiaryKey.createKey(user.getUserId(), diaryId);
        Optional<LikeDiary> likeDiary = likeDiaryRepository.findById(key);

        boolean isLike = likeDiary.isPresent() ? true : false;

        // 좋아요 개수
        Long likeNum = likeDiaryRepository.countLikesByDiaryId(diaryId);

        // 내 일기인지 확인
        boolean isMine = (diary.getUsers() == user) ? true : false;

        // 조회 권한 확인
        Friend friend = friendRepository.findMyFriendToEntity(user.getUserId(), diary.getUsers().getUserId());

        // 내 일기 아님 + 친구 아님 + 친구공개
        if(!isMine && friend == null && diary.getOpenType().getValue() == "친구공개"){
            throw new DiaryForbiddenException("[FIND_DIARY] " + DiaryExceptionMessage.DIARY_HANDLE_ACCESS_DENIED.getMsg());
        }

        // 내 일기 아님 + 비공개
        if(!isMine && diary.getOpenType().getValue() == "비공개") {
            throw new DiaryForbiddenException("[FIND_DIARY] " + DiaryExceptionMessage.DIARY_HANDLE_ACCESS_DENIED.getMsg());
        }

        // 내 일기 or 친구인데 친구공개 or 공개
        return DiaryResDto.createDto(diary, likeNum, isLike, isMine);
    }

    /**
     * 일기 조회 (월별)
     * @param accessToken String
     * @param date String
     * @return DiaryMonthlyResDto[]
     */
    @Override
    @Transactional
    public DiaryMonthlyResDto[] findDiaryByMonth(String accessToken, String date) {

        // 입력값 검증
        if(date == null || date.isEmpty() || accessToken == null) {
            throw new DiaryBadRequestException("[FIND_DIARY_BY_MONTH] " + DiaryExceptionMessage.DIARY_BAD_REQUEST.getMsg());
        }

        // 사용자 확인
        Users user = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[FIND_DIARY_BY_MONTH] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        List<DiaryMonthlyResDto> diaries = diaryRepository.findByUsersAndYearMonth(user, date);

        DiaryMonthlyResDto[] monthly = new DiaryMonthlyResDto[32];

        // 인덱스에 해당하는 날짜의 일기 넣어주기
        for(DiaryMonthlyResDto diary : diaries) {
            int day = Integer.parseInt(diary.getDate().substring(8, 10));
            monthly[day] = diary;
        }

        return monthly;
    }

    /**
     * 친구 일기 조회 (월별)
     * @param accessToken String
     * @param date String
     * @param friendId Long
     * @return DiaryMonthlyResDto[]
     */
    @Override
    @Transactional
    public DiaryMonthlyResDto[] findFriendDiaryByMonth(String accessToken, String date, Long friendId) {

        // 입력값 검증
        if(date == null || date.isEmpty() || accessToken == null || friendId == null) {
            throw new DiaryBadRequestException("[FIND_FRIEND_DIARY_BY_MONTH] " + DiaryExceptionMessage.DIARY_BAD_REQUEST.getMsg());
        }

        // 사용자 확인
        Users user = userRepository.findById(jwtTokenProvider.getUserId(accessToken))
                .orElseThrow(() -> new UserNotFoundException("[FIND_FRIEND_DIARY_BY_MONTH] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        Users friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("[FIND_FRIEND_DIARY_BY_MONTH] " + UserExceptionMessage.USER_NOT_FOUND.getMsg()));

        // 조회 권한 확인
        Friend isFriend = friendRepository.findMyFriendToEntity(user.getUserId(), friendId);

        log.info("사용자 조회 : " + user.getNickname());
        log.info("친구 조회 : " + friend.getNickname());
        log.info("친구 관계 : " + isFriend);


        List<DiaryMonthlyResDto> diaries;
        if(isFriend != null){
            diaries = diaryRepository.findByUsersAndYearMonthAAndOpenTypeNot(friend, date, OpenType.getOpenType("비공개"));
        } else {
            diaries = diaryRepository.findByUsersAndYearMonthAAndOpenType(friend, date, OpenType.getOpenType("공개"));
        }

        DiaryMonthlyResDto[] monthly = new DiaryMonthlyResDto[32];

        for(DiaryMonthlyResDto diary : diaries) {
            int day = Integer.parseInt(diary.getDate().substring(8, 10));
            monthly[day] = diary;
        }
        return monthly;
    }


}
