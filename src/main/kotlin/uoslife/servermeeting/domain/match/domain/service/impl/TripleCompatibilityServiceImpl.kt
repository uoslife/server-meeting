package uoslife.servermeeting.domain.match.domain.service.impl

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.match.domain.service.CompatibilityService
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.service.impl.MeetingTeamService
import java.util.concurrent.ConcurrentHashMap

/*
### InfoDistance 17

// 서로 비교
1. prefer day
    1. 7bit
// 서로 비교
2. Q&A
    1. 10bit
    2. 2+2+3+3

### InfoFilter 74

// user에서 긁어와서 비교
1. prefer age
    1. 32bit
    2. 16 + 16
// user에서 긁어와서 비교
2. department
    1. 39bit
// 서로 비교
3. mood
    1. 3bit
 */
@Service
@Transactional
class TripleCompatibilityServiceImpl(
    private val compatibilityUtils: CompatibilityUtils,
    private val meetingTeamService: MeetingTeamService,
    @Value("\${app.tripleInformationFilterDelimiterParams}") private val tripleInformationFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    @Value("\${app.triplePreferenceFilterDelimiterParams}") private val triplePreferenceFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,

) : CompatibilityService {
    override fun isMatchedOnFilterCompatibility(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
        informationFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
        preferenceFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): Boolean {
        val teamFilterInfo =
            compatibilityUtils.parseBitInfo(team.information!!.filterInfo, informationFilterDelimiterParams)
        val opponentTeamFilterInfo =
            compatibilityUtils.parseBitInfo(opponentTeam.information!!.filterInfo, informationFilterDelimiterParams)

        teamFilterInfo.forEach { (key, value) ->
            when (key) {
                "age" -> {
                    val startIdx = value.substring(0, 16).indexOf('1')
                    val endIdx = value.substring(16, 32).indexOf('1')
                    meetingTeamService.getTeamAgeByMeetingTeam(opponentTeam).forEach { age ->
                        if (compatibilityUtils.getAgeBitPosition(age) !in startIdx..endIdx) {
                            return false
                        }
                    }
                }

                "department" -> {
                    meetingTeamService.getTeamDepartmentByMeetingTeam(opponentTeam).forEach { department ->
                        if (value[compatibilityUtils.getDepartmentBitPosition(department)] == '1') {
                            return false
                        }
                    }
                }

                "mood" -> {
                    val opponentTeamMood = opponentTeamFilterInfo["mood"]!!
                    if (value != opponentTeamMood) {
                        return false
                    }
                }
            }
        }

        if (!compatibilityUtils.hasDifferentTeamGender(team, opponentTeam)) {
            return false
        }
        if (!compatibilityUtils.hasSameTeamType(team, opponentTeam)) {
            return false
        }

        return true
    }

    override fun calculateDistanceCompatibility(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
        preferenceDistanceDelimiterParams: ConcurrentHashMap<String, List<Int>>,
        informationDistanceDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): Int {
        TODO("Not yet implemented")
    }

    override fun saveCompatibilityScore(team: MeetingTeam, opponentTeam: MeetingTeam) {
        TODO("Not yet implemented")
    }
}
