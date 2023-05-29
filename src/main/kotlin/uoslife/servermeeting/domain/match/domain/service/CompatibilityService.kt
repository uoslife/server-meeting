package uoslife.servermeeting.domain.match.domain.service

import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam

interface CompatibilityService {

    fun isFilteredOnCompatibility(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
    ): Boolean
    fun calculateDistanceCompatibility(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
    ): Int
    fun getCompatibilityScore(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
    ): Int
    fun saveCompatibilityScore(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
        score: Int,
    )
}
