package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class TeamCodeGenerateFailedException : BusinessException(ErrorCode.TEAM_CODE_GENERATE_FAILED)
