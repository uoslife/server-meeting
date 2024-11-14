package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class GenderNotUpdatedException : EntityNotFoundException(ErrorCode.GENDER_NOT_FOUND)
