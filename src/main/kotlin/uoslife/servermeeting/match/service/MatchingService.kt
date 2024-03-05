package uoslife.servermeeting.match.service

import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.match.exception.InvalidMatchException
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.match.repository.MatchRepository
import uoslife.servermeeting.meetingteam.dao.MeetingTeamDao
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.Compatibility
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.CompatibilityNotFoundException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val userRepository: UserRepository,
    private val meetingTeamDao: MeetingTeamDao,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val matchRepository: MatchRepository,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    @Transactional
    fun getMatchedMeetingTeam(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val meetingTeam = user.team ?: throw MeetingTeamNotFoundException()
        val opponentTeam =
            when (user.gender) {
                GenderType.MALE -> match.femaleTeam
                GenderType.FEMALE -> match.maleTeam
            }
                ?: throw MatchNotFoundException()
        val opponentUser = opponentTeam.leader ?: throw UserNotFoundException()

        return when (meetingTeam.type) {
            SINGLE -> {
                singleMeetingService.getMeetingTeamInformation(
                    UUID.fromString(opponentUser.id.toString()) ?: throw UserNotFoundException()
                )
            }
            TRIPLE -> {
                tripleMeetingService.getMeetingTeamInformation(
                    UUID.fromString(opponentUser.id.toString()) ?: throw UserNotFoundException()
                )
            }
        }
    }

    @Transactional
    fun matchUser(type: TeamType) {
        val maleUnmatchedList =
            meetingTeamDao.findMatchingMeetingTeamByTeamTypeAndGenderType(type, GenderType.MALE)
        val femaleUnmatchedList =
            meetingTeamDao.findMatchingMeetingTeamByTeamTypeAndGenderType(type, GenderType.FEMALE)

        while (!maleUnmatchedList.isEmpty()) {
            val male = maleUnmatchedList.get(0)
            val maleCompatibility = male.compatibility
            if (maleCompatibility == null) throw CompatibilityNotFoundException()
            val maleScore = maleCompatibility.score
            var maleMatchSequence = maleCompatibility.matchSequence

            val female = meetingTeamRepository.findByIdOrNull(maleScore.get(maleMatchSequence)) ?: throw MeetingTeamNotFoundException()
            val femaleCompatibility = female.compatibility
            if (femaleCompatibility == null) throw CompatibilityNotFoundException()
            val femaleScore = femaleCompatibility.score

            if (!matchRepository.existsByFemaleTeam(female)) {
                val match = Match(
                    date = LocalDateTime.now(),
                    maleTeam = male,
                    femaleTeam = female
                )
                matchRepository.save(match)
            }
            else { // else if (matchRepository.existsByFemaleTeam(female))
                val match = matchRepository.findByFemaleTeam(female) ?: throw InvalidMatchException()
                val matchedMale = match.maleTeam
                if (femaleScore.indexOf(male.id) < femaleScore.indexOf(matchedMale.id)) {
                    match.maleTeam = male
                    match.femaleTeam = female
                    matchRepository.save(match)
                }
            }
            maleMatchSequence += 1
            meetingTeamRepository.save(male)
        }
    }

    @Transactional
    fun setSingleCompatibility() {
        // TODO: 가중치에 따라 COMPATIBILITY 설정
        // single
        val maleMeetingTeamList =
            meetingTeamDao.findMatchingMeetingTeamByTeamTypeAndGenderType(SINGLE, GenderType.MALE)
        val femaleMeetingTeamList =
            meetingTeamDao.findMatchingMeetingTeamByTeamTypeAndGenderType(SINGLE, GenderType.FEMALE)

        // 남성 대상 선호 리스트 결정
        for (male in maleMeetingTeamList) {
            for (female in femaleMeetingTeamList) {}
        }

        // 여성 대상 선호 리스트 결정
        for (female in femaleMeetingTeamList) {
            for (male in maleMeetingTeamList) {}
        }
    }

    @Transactional
    fun setTripleCompatibility() {
        // triple
        val maleMeetingTeamList =
            meetingTeamDao.findMatchingMeetingTeamByTeamTypeAndGenderType(TRIPLE, GenderType.MALE)
        val femaleMeetingTeamList =
            meetingTeamDao.findMatchingMeetingTeamByTeamTypeAndGenderType(TRIPLE, GenderType.FEMALE)

        // 남성 대상 선호 리스트 결정
        for (male in maleMeetingTeamList) {
            for (female in femaleMeetingTeamList) {}
        }

        // 여성 대상 선호 리스트 결정
        for (female in femaleMeetingTeamList) {
            for (male in maleMeetingTeamList) {}
        }
    }
}
