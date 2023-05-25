package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class TeamFullException : BusinessException(ErrorCode.TEAM_FULL)
