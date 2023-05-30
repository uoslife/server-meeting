package uoslife.servermeeting.domain.meeting.domain.service.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.meeting.domain.dao.MeetingTeamDao
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.Preference
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.repository.InformationRepository
import uoslife.servermeeting.domain.meeting.domain.repository.PreferenceRepository
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType

@Service
@Transactional
class MeetingTeamService(
    private val meetingTeamDao: MeetingTeamDao,
    private val preferenceRepository: PreferenceRepository,
    private val informationRepository: InformationRepository,
    private val userTeamDao: UserTeamDao,
) {
    fun getMeetingTeamInformationByMeetingTeam(meetingTeam: MeetingTeam): Information? {
        return informationRepository.findByMeetingTeam(meetingTeam)
    }
    fun getMeetingTeamPreferenceByMeetingTeam(meetingTeam: MeetingTeam): Preference? {
        return preferenceRepository.findByMeetingTeam(meetingTeam)
    }
    fun getCountMeetingTeamByGender(genderType: GenderType, teamType: TeamType): Int {
        return meetingTeamDao.countMeetingTeamByGenderAndTeamType(genderType, teamType)
    }
    fun getMeetingTeamByGender(genderType: GenderType, teamType: TeamType): List<MeetingTeam> {
        return meetingTeamDao.findMeetingTeamByGenderAndTeamType(genderType, teamType)
    }
    fun getTeamTypeByMeetingTeam(meetingTeam: MeetingTeam): TeamType {
        return userTeamDao.findByTeam(meetingTeam).firstOrNull()?.type ?: TeamType.SINGLE
    }
    fun getGenderByMeetingTeam(meetingTeam: MeetingTeam): GenderType? {
        return meetingTeamDao.getGenderTypeByMeetingTeam(meetingTeam)
    }
    fun getTeamDepartmentByMeetingTeam(meetingTeam: MeetingTeam): List<DepartmentNameType> {
        return meetingTeamDao.getTeamDepartmentByMeetingTeam(meetingTeam)
    }
    fun getTeamAgeByMeetingTeam(meetingTeam: MeetingTeam): List<Int> {
        return meetingTeamDao.getTeamAgeByMeetingTeam(meetingTeam)
    }
}
