package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class KakaoTalkIdDuplicationException : BusinessException(ErrorCode.KAKAO_TALK_ID_DUPLICATED) {}
