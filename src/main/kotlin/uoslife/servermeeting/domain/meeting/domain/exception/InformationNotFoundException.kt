package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class InformationNotFoundException : EntityNotFoundException(ErrorCode.INFORMATION_NOT_FOUND)
