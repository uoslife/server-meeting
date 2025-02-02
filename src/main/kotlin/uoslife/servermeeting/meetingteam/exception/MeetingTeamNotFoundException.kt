package uoslife.servermeeting.meetingteam.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

class MeetingTeamNotFoundException : EntityNotFoundException(ErrorCode.MEETING_TEAM_NOT_FOUND)
