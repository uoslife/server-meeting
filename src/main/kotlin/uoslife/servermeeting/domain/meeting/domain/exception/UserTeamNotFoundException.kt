package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UserTeamNotFoundException : EntityNotFoundException(ErrorCode.USER_TEAM_NOT_FOUND)
