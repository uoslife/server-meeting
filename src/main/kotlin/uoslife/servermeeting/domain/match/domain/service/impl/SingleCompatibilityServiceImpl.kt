package uoslife.servermeeting.domain.match.domain.service.impl

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.match.domain.dao.CompatibilityDao
import uoslife.servermeeting.domain.match.domain.service.CompatibilityService
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.service.impl.MeetingTeamService
import java.util.concurrent.ConcurrentHashMap

/**
 * description: CompatibilityServiceImpl
 * details: compare two meeting team and calculate compatibility score to save score in compatibility table
 * ## SINGLE
 *
 * ### InformationDistance (38bit)
 *
 * 1. height
 *     1. 3-digit integer
 *
 * 2. Q&A
 *     1. 8bit
 *     2. 2+2+2+2
 * 3. MBTI
 *     1. 8bit
 *     2. EINSFTJP
 * 4. face type
 *     1. 9bit
 *     2. mark 1 animal
 *     3. max 2
 * 5. interests
 *     1. 10bit
 *     2. 1 for the corresponding interest
 *     3. can chose 3
 *
 * ### InformationFilter (60bit)
 *
 * 1. age
 *     1. 16bit
 *     2. 20 to 35 years old
 * 2. smoking
 *     1. 2bit
 *     2. smoking 10, non-smoking 01
 * 3. departmet
 *     1. 39bit
 * 4. identity
 *     1. 3bit
 *     2. undergraduate 100 graduate 010 graduate 001
 *
 * ### PreferenceDistance (23bit)
 *
 * 1. height (range)
 *     1. start range 3 digits int
 *     2. end range 3 digits int
 * 2. MBTI
 *     1. 8bit
 * 3. face type
 *     1. 9bit
 *
 * ### PreferenceFilter (77bit)
 *
 * 1. age (range)
 *     1. start range 16bit
 *     2. end range 16bit
 * 2. smoking
 *     1. 3bit
 *     2. smoking 100 non-smoking 010 regardless 001
 * 3. department
 *     1. 39bit
 * 4. identity
 *     1. 3bit
 *     2. undergraduate 100 college student 010 graduate 001
 *
 *
 */
@Service
@Transactional
class SingleCompatibilityServiceImpl(
    private val meetingTeamService: MeetingTeamService,
    private val compatibilityDao: CompatibilityDao,
    private val compatibilityUtils: CompatibilityUtils,
    @Value("\${app.preferenceDistanceDelimiters}") private val preferenceDistanceDelimiters: ConcurrentHashMap<String, List<Int>>,
    @Value("\${app.informationDistanceDelimiters}") private val informationDistanceDelimiters: ConcurrentHashMap<String, List<Int>>,
    @Value("\${app.preferenceFilterDelimiters}") private val preferenceFilterDelimiters: ConcurrentHashMap<String, List<Int>>,
    @Value("\${app.informationFilterDelimiters}") private val informationFilterDelimiters: ConcurrentHashMap<String, List<Int>>,
) : CompatibilityService {

    /**
     * description: isFilteredOnCompatibility
     * details: check if two meeting team is filtered on compatibility
     * 두 팀이 성별이 달라야 하고, 팀 타입이 같아야 함
     * information filter 와 preference filter 를 비교하여 필터링
     */
    override fun isMatchedOnFilterCompatibility(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
        informationFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
        preferenceFilterDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): Boolean {
        //TODO(null check) 매칭 정보가 없음
        val opponentTeamInformationFilter =
            compatibilityUtils.parseBitInfo(opponentTeam.information!!.filterInfo, informationFilterDelimiterParams)
        //TODO(null check) 매칭 정보가 없음
        val teamPreferenceFilter =
            compatibilityUtils.parseBitInfo(team.preference.first().filterCondition, preferenceFilterDelimiterParams)

        teamPreferenceFilter.forEach { (key, value) ->
            when (key) {
                "age" -> {
                    if (!compatibilityUtils.isAgeInRange(
                            opponentTeamInformationFilter[key]!!,
                            value,
                        )
                    ) {
                        return false
                    }
                }

                "smoking" -> {
                    if (!compatibilityUtils.isSmokingPreferenceMatched(
                            opponentTeamInformationFilter[key]!!,
                            value,
                        )
                    ) {
                        return false
                    }
                }

                "department" -> {
                    if (!compatibilityUtils.isDepartmentMatched(
                            opponentTeamInformationFilter[key]!!,
                            value,
                        )
                    ) {
                        return false
                    }
                }

                "identity" -> {
                    if (compatibilityUtils.isIdentityMatched(
                            opponentTeamInformationFilter[key]!!,
                            value,
                        )
                    ) {
                        return false
                    }
                }
            }
            if (!compatibilityUtils.hasDifferentTeamGender(team, opponentTeam)) {
                return false
            }
            if (!compatibilityUtils.hasSameTeamType(team, opponentTeam)) {
                return false
            }
        }
        return true
    }

    override fun calculateDistanceCompatibility(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
        preferenceDistanceDelimiterParams: ConcurrentHashMap<String, List<Int>>,
        informationDistanceDelimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): Int {
        val teamPreferenceDistance = compatibilityUtils.parseBitInfo(
            team.preference.first().distanceCondition,
            preferenceDistanceDelimiterParams,
        )
        val opponentTeamInformationDistance = compatibilityUtils.parseBitInfo(
            opponentTeam.information!!.distanceInfo,
            informationDistanceDelimiterParams,
        )
        val teamInformationDistance =
            compatibilityUtils.parseBitInfo(team.information!!.distanceInfo, informationDistanceDelimiterParams)
        var compatibilityScore: Int = 0

        teamPreferenceDistance.forEach { (key, value) ->
            when (key) {
                "height" -> {
                    compatibilityScore += compatibilityUtils.calculateHeightCompatibility(
                        value,
                        opponentTeamInformationDistance[key]!!,
                    )
                }

                "MBTI" -> {
                    compatibilityScore += compatibilityUtils.calculateMBTICompatibility(
                        value,
                        opponentTeamInformationDistance[key]!!,
                    )
                }

                "face type" -> {
                    compatibilityScore += compatibilityUtils.calculateFaceTypeCompatibility(
                        value,
                        opponentTeamInformationDistance[key]!!,
                    )
                }

                "Q&A" -> {
                    compatibilityScore += compatibilityUtils.calculateQnACompatibility(
                        teamInformationDistance[key]!!,
                        opponentTeamInformationDistance[key]!!,
                    )
                }

                "interests" -> {
                    compatibilityScore += compatibilityUtils.calculateInterestsCompatibility(
                        teamInformationDistance[key]!!,
                        opponentTeamInformationDistance[key]!!,
                    )
                }
            }
        }
        return compatibilityScore
    }

    override fun saveCompatibilityScore(team: MeetingTeam, opponentTeam: MeetingTeam) {
        if (isMatchedOnFilterCompatibility(
                team,
                opponentTeam,
                informationFilterDelimiters,
                preferenceFilterDelimiters,
            )
        ) {
            return compatibilityDao.saveCompatibilityScore(team, opponentTeam, 0)
        }
        val score = calculateDistanceCompatibility(
            team,
            opponentTeam,
            preferenceDistanceDelimiters,
            informationDistanceDelimiters,
        )
        return compatibilityDao.saveCompatibilityScore(team, opponentTeam, score)
    }
}
