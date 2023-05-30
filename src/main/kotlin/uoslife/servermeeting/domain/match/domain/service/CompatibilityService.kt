package uoslife.servermeeting.domain.match.domain.service

import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import java.util.concurrent.ConcurrentHashMap

interface CompatibilityService {

    fun isMatchedOnFilterCompatibility(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
        informationFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
        preferenceFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): Boolean

    fun calculateDistanceCompatibility(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
        preferenceDistanceDelimiterParams: ConcurrentHashMap<String, List<Int>>,
        informationDistanceDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): Int

    fun saveCompatibilityScore(team: MeetingTeam, opponentTeam: MeetingTeam)
}
