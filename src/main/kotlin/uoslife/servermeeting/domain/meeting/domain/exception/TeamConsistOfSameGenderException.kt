package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class TeamConsistOfSameGenderException : BusinessException(ErrorCode.TEAM_CONSIST_OF_SAME_GENDER)
