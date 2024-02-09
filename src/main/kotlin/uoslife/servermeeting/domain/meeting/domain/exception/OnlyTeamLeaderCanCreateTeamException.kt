package uoslife.servermeeting.domain.meeting.domain.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class OnlyTeamLeaderCanCreateTeamException :
    AccessDeniedException(ErrorCode.ONLY_TEAM_LEADER_CAN_CREATE_TEAM)
