package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.AccessDeniedException
import uoslife.servermeeting.global.error.exception.ErrorCode

class OnlyTeamLeaderCanUpdateTeamInformationException :
    AccessDeniedException(ErrorCode.ONLY_TEAM_LEADER_CAN_UPDATE_TEAM_INFORMATION)
