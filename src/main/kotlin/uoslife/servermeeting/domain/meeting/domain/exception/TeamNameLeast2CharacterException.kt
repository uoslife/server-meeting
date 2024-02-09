package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class TeamNameLeast2CharacterException :
    InvalidValueException(ErrorCode.TEAM_NAME_LEAST_2_CHARACTER)
