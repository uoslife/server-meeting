package uoslife.servermeeting.domain.match.domain.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class MatchNotFoundException : BusinessException(ErrorCode.MATCH_NOT_FOUND)
