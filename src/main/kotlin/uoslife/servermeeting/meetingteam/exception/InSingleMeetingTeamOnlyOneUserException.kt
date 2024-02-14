package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.ErrorCode

class InSingleMeetingTeamOnlyOneUserException :
    BusinessException(ErrorCode.IN_SINGLE_MEETING_TEAM_ONLY_ONE_USER)
