package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class PreferenceNotFoundException : EntityNotFoundException(ErrorCode.PREFERENCE_NOT_FOUND)
