package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class OnlyTeamLeaderCanGetMatchException :
    AccessDeniedException(ErrorCode.ONLY_TEAM_LEADER_CAN_GET_MATCH)
