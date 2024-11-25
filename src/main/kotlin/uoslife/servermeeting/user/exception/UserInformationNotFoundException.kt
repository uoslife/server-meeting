package uoslife.servermeeting.user.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserInformationNotFoundException : EntityNotFoundException(ErrorCode.INFORMATION_NOT_FOUND)
