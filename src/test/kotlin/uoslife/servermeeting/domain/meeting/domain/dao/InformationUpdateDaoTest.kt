package uoslife.servermeeting.domain.meeting.domain.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.meeting.domain.common.InformationTest
import uoslife.servermeeting.domain.meeting.domain.entity.Information

class InformationUpdateDaoTest : InformationTest() {

    @Test
    fun `Information 없는 상황에서 정상적으로 저장`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val information = informationRepository.save(
            Information(
                meetingTeam = meetingTeam,
                meetingTime = "0010000",
                filterInfo = "0000000",
                distanceInfo = "1000000",
            ),
        )
        entityManager.flush()
        entityManager.clear()

        // then
        val informationFindByMeetingTeam = informationRepository.findByMeetingTeam(meetingTeam)
        assertThat(informationFindByMeetingTeam).isNotNull
        assertThat(informationFindByMeetingTeam?.filterInfo).isEqualTo("0000000")
        assertThat(informationFindByMeetingTeam?.distanceInfo).isEqualTo("1000000")
        assertThat(informationFindByMeetingTeam?.meetingTime).isEqualTo("0010000")
    }

    @Test
    fun `Information이 이미 존재하는 상황에서 정상적으로 업데이트`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val information = informationRepository.save(
            Information(
                meetingTeam = meetingTeam,
                meetingTime = "0010000",
                filterInfo = "0000000",
                distanceInfo = "1000000",
            ),
        )
        val informationId = information.id

        entityManager.flush()
        entityManager.clear()

        informationUpdateDao.updateInformationByMeetingTeam(
            meetingTeam = meetingTeam,
            meetingTime = "1111111",
            filterInfo = "1111110",
            distanceInfo = "1111101",
        )

        // then
        val fixedMeetingTeam = informationRepository.findByMeetingTeam(meetingTeam)
        assertThat(fixedMeetingTeam).isNotNull
        assertThat(fixedMeetingTeam?.filterInfo).isEqualTo("1111110")
        assertThat(fixedMeetingTeam?.distanceInfo).isEqualTo("1111101")
        assertThat(fixedMeetingTeam?.meetingTime).isEqualTo("1111111")
    }
}
