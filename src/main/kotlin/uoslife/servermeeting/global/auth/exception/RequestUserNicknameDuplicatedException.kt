package uoslife.servermeeting.global.auth.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class RequestUserNicknameDuplicatedException :
    AccessDeniedException(ErrorCode.REQUEST_USER_NICKNAME_DUPLICATED) {}
