package uoslife.servermeeting.domain.match.domain.service.impl

import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.service.impl.MeetingTeamService
import java.util.concurrent.ConcurrentHashMap

/**
 * description: CompatibilityServiceImpl
 * details: compare two meeting team and calculate compatibility score to save score in compatibility table
 */
@Service
class CompatibilityServiceImpl(
    private val meetingTeamService: MeetingTeamService,
) {
    /**
     * description: isFilteredOnCompatibility
     * details: check if two meeting team is filtered on compatibility
     * 두 팀이 성별이 달라야 하고, 팀 타입이 같아야 함
     * information filter 와 preference filter 를 비교하여 필터링
     */
    fun isFilteredOnCompatibility(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
        infoDelimiterParams: ConcurrentHashMap<String, Int>,
        preferenceDelimiterParams: ConcurrentHashMap<String, Int>,
    ): Boolean {
        val teamInfo = parseBitInfo(team.information!!.filterInfo, infoDelimiterParams)
        val otherTeamInfo = parseBitInfo(otherTeam.information!!.filterInfo, infoDelimiterParams)

        val teamPreference = parseBitInfo(team.preference.first().filterCondition, preferenceDelimiterParams)
        val otherTeamPreference = parseBitInfo(otherTeam.preference.first().filterCondition, preferenceDelimiterParams)
        //TODO: 필터링 조건에 따라 필터링
        return true
    }

    private fun hasDifferentTeamGender(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
    ) = meetingTeamService.getGenderByMeetingTeam(team) != meetingTeamService.getGenderByMeetingTeam(otherTeam)

    private fun hasSameTeamType(
        team: MeetingTeam,
        otherTeam: MeetingTeam,
    ) = meetingTeamService.getTeamTypeByMeetingTeam(team) == meetingTeamService.getTeamTypeByMeetingTeam(otherTeam)

    fun parseBitInfo(info: String, delimiterParams: ConcurrentHashMap<String, Int>): ConcurrentHashMap<String, Int> {
        //TODO: delimiterParams의 합과 info의 길이가 같은지 확인
        val filters: ConcurrentHashMap<String, Int> = ConcurrentHashMap()
        var startIndex = 0
        for (delimiter in delimiterParams) {
            val filter = info.substring(startIndex, startIndex + delimiter.value)
            filters[delimiter.key] = filter.toInt()
            startIndex += delimiter.value
        }
        return filters
    }
    fun compareSimpleFilterInfo(info: List<String>, otherInfo: List<String>): Boolean {
        for (i in info.indices) {
            if (info[i] != otherInfo[i]) {
                return false
            }
        }
        return true
    }
    fun isAgeInBinaryRange(age: String, ageRange: String): Boolean {
        val start = ageRange.substring(0, 7).toInt()
        val end = ageRange.substring(7, 14).toInt()

        return age.toInt() in start..end
    }
    fun isSmokingPreferenceMatched(smoking: String, smokingPreference: String): Boolean {
        when (smokingPreference) {
            "10" -> return true
            "00" -> return smoking == "0"
            "01" -> return smoking == "1"
            else -> return false //TODO(예외처리)
        }
    }
    fun calculateDistanceCompatibility(team: MeetingTeam, otherTeam: MeetingTeam): Int {
        TODO("Not yet implemented")
    }

    fun getCompatibilityScore(team: MeetingTeam, otherTeam: MeetingTeam): Int {
        TODO("Not yet implemented")
    }

    fun saveCompatibilityScore(team: MeetingTeam, otherTeam: MeetingTeam, score: Int) {
        TODO("Not yet implemented")
    }
}
