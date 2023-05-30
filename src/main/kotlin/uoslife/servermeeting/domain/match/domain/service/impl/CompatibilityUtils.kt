package uoslife.servermeeting.domain.match.domain.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.match.domain.dao.CompatibilityDao
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.service.impl.MeetingTeamService
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

@Service
@Transactional
class CompatibilityUtils(
    private val meetingTeamService: MeetingTeamService,
    private val compatibilityDao: CompatibilityDao,
) {
    fun calculateSameLengthComponent(teamInfo: String, opponentTeamInfo: String) =
        (teamInfo.toInt() and opponentTeamInfo.toInt()).toString(2).count { it == '1' }

    fun isDepartmentMatched(
        opponentTeamInformationAboutDepartment: String,
        teamPreferenceFilterAboutDepartment: String,
    ): Boolean {
        if (opponentTeamInformationAboutDepartment.toInt() and teamPreferenceFilterAboutDepartment.toInt() != 0) {
            return true
        }
        return false
    }

    fun hasDifferentTeamGender(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
    ) = meetingTeamService.getGenderByMeetingTeam(team) != meetingTeamService.getGenderByMeetingTeam(opponentTeam)

    fun hasSameTeamType(
        team: MeetingTeam,
        opponentTeam: MeetingTeam,
    ): Boolean =
        meetingTeamService.getTeamTypeByMeetingTeam(team) == meetingTeamService.getTeamTypeByMeetingTeam(opponentTeam)

    fun parseBitInfo(
        info: String,
        delimiterParams: ConcurrentHashMap<String, List<Int>>,
    ): ConcurrentHashMap<String, String> {
        //TODO: delimiterParams의 값이 parsing 범위 밖인지 확인
        //TODO: delimiterParams의 값이 없을시 예외처리
        val filters: ConcurrentHashMap<String, String> = ConcurrentHashMap()
        delimiterParams.forEach { (key, value) ->
            filters[key] = info.substring(value[0], value[1])
        }
        return filters
    }

    fun isAgeInRange(age: String, ageRange: String): Boolean {
        val start = ageRange.substring(0, 3).toInt()
        val end = ageRange.substring(3, 6).toInt()

        return age.toInt() in start..end
    }

    fun isSmokingPreferenceMatched(smokingInformation: String, smokingPreference: String): Boolean {
        return when (smokingPreference) {
            "100" -> smokingInformation == "10"
            "010" -> smokingInformation == "01"
            "001" -> true
            else -> false //TODO(예외처리)
        }
    }

    fun isIdentityMatched(
        teamPreferenceFilterAboutIdentity: String,
        opponentTeamInformationFilterAboutIdentity: String,

    ): Boolean {
        if (opponentTeamInformationFilterAboutIdentity!! != teamPreferenceFilterAboutIdentity!!) {
            return true
        }
        return false
    }

    fun getCompatibilityScore(team: MeetingTeam, opponentTeam: MeetingTeam): Int {
        return compatibilityDao.findCompatibilityScoreByTeam(team, opponentTeam)
    }

    fun calculateInterestsCompatibility(
        teamInfoAboutInterests: String,
        opponentTeamInfoAboutInterests: String,
    ): Int {
        return calculateSameLengthComponent(teamInfoAboutInterests, opponentTeamInfoAboutInterests)
    }

    fun calculateQnACompatibility(teamInformationAboutQA: String, opponentTeamInfoAboutQA: String): Int {
        return calculateSameLengthComponent(teamInformationAboutQA, opponentTeamInfoAboutQA)
    }

    fun calculateFaceTypeCompatibility(
        teamPreferenceAboutFaceType: String,
        opponentTeamInfoAboutFaceType: String,
    ): Int {
        return calculateSameLengthComponent(teamPreferenceAboutFaceType, opponentTeamInfoAboutFaceType)
    }

    fun calculateMBTICompatibility(teamPreferenceAboutHeight: String, opponentTeamInfoAboutHeight: String): Int {
        return calculateSameLengthComponent(teamPreferenceAboutHeight, opponentTeamInfoAboutHeight)
    }

    fun calculateHeightCompatibility(teamPreferenceAboutHeight: String, opponentTeamInfoAboutHeight: String): Int {
        val start = teamPreferenceAboutHeight.substring(0, 3).toInt()
        val end = teamPreferenceAboutHeight.substring(3, 6).toInt()
        val height = opponentTeamInfoAboutHeight.toInt()

        return if (height in start..end) {
            100
        } else {
            (100 / min(abs(start - height), abs(end - height)).toDouble().pow(2).toInt())
        }
    }

    fun getDepartmentBitPosition(department: DepartmentNameType): Int {
        return when (department) {
            DepartmentNameType.ADMINISTRATION -> 0
            DepartmentNameType.INTERNATIONAL_RELATIONS -> 1
            DepartmentNameType.ECONOMICS -> 2
            DepartmentNameType.SOCIAL_WELFARE -> 3
            DepartmentNameType.TAXATION -> 4
            DepartmentNameType.BUSINESS -> 5
            DepartmentNameType.CHEMICAL_ENGINEERING -> 6
            DepartmentNameType.MECHATRONICS -> 7
            DepartmentNameType.MATERIALS_ENGINEERING -> 8
            DepartmentNameType.ELECTRICAL_ENGINEERING -> 9
            DepartmentNameType.CIVIL_ENGINEERING -> 10
            DepartmentNameType.COMPUTER_SCIENCE -> 11
            DepartmentNameType.ENVIRONMENTAL_HORTICULTURE -> 12
            DepartmentNameType.MATHEMATICS -> 13
            DepartmentNameType.PHYSICS -> 14
            DepartmentNameType.LIFE_SCIENCES -> 15
            DepartmentNameType.STATISTICS -> 16
            DepartmentNameType.ENGLISH_LITERATURE -> 17
            DepartmentNameType.KOREAN_LITERATURE -> 18
            DepartmentNameType.HISTORY -> 19
            DepartmentNameType.PHILOSOPHY -> 20
            DepartmentNameType.CHINESE_CULTURE -> 21
            DepartmentNameType.URBAN_ADMINISTRATION -> 22
            DepartmentNameType.URBAN_SOCIOLOGY -> 23
            DepartmentNameType.GEOINFORMATION_ENGINEERING -> 24
            DepartmentNameType.ARCHITECTURE_ARCHITECTURAL -> 25
            DepartmentNameType.ARCHITECTURE_ENGINEERING -> 26
            DepartmentNameType.URBAN_ENGINEERING -> 27
            DepartmentNameType.TRANSPORTATION_ENGINEERING -> 28
            DepartmentNameType.LANDSCAPE_ARCHITECTURE -> 29
            DepartmentNameType.ENVIRONMENTAL_ENGINEERING -> 30
            DepartmentNameType.MUSIC -> 31
            DepartmentNameType.INDUSTRIAL_DESIGN_VISUAL -> 32
            DepartmentNameType.INDUSTRIAL_DESIGN_INDUSTRIAL -> 33
            DepartmentNameType.ENVIRONMENTAL_SCULPTURE -> 34
            DepartmentNameType.SPORTS_SCIENCE -> 35
            DepartmentNameType.LIBERAL_ARTS -> 36
            DepartmentNameType.APPLIED_CHEMISTRY -> 37
            DepartmentNameType.ARTIFICIAL_INTELLIGENCE -> 38
        }
    }
    fun getAgeBitPosition(age: Int): Int {
        return when (age) {
            20 -> 0
            21 -> 1
            22 -> 2
            23 -> 3
            24 -> 4
            25 -> 5
            26 -> 6
            27 -> 7
            28 -> 8
            29 -> 9
            30 -> 10
            31 -> 11
            32 -> 12
            33 -> 13
            34 -> 14
            35 -> 15
            else -> {
                throw IllegalArgumentException("나이가 범위를 벗어났습니다.") //TODO(error code)
            }
        }
    }
}
