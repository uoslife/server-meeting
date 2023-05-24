package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class InSingleMeetingTeamNoJoinTeamException : BusinessException(ErrorCode.IN_SINGLE_MEETING_TEAM_NO_JOIN_TEAM)
