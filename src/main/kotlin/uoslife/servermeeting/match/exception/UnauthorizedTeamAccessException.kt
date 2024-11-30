package uoslife.servermeeting.match.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class UnauthorizedTeamAccessException : AccessDeniedException(ErrorCode.UNAUTHORIZED_TEAM_ACCESS)
