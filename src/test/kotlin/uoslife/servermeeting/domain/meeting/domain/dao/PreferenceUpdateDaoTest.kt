package uoslife.servermeeting.domain.meeting.domain.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.meeting.domain.common.PreferenceTest
import uoslife.servermeeting.domain.meeting.domain.entity.Preference

class PreferenceUpdateDaoTest : PreferenceTest() {

    @Test
    fun `Preference 없는 상황에서 정상적으로 저장`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val preference = preferenceRepository.save(
            Preference(
                meetingTeam = meetingTeam,
                filterCondition = "0000000",
                distanceCondition = "1000000",
            ),
        )
        entityManager.flush()
        entityManager.clear()

        // then
        val preferenceFindByMeetingTeam = preferenceRepository.findByMeetingTeam(meetingTeam)
        assertThat(preferenceFindByMeetingTeam).isNotNull
        assertThat(preferenceFindByMeetingTeam?.filterCondition).isEqualTo("0000000")
        assertThat(preferenceFindByMeetingTeam?.distanceCondition).isEqualTo("1000000")
    }

    @Test
    fun `Preference가 이미 존재하는 상황에서 정상적으로 업데이트`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val preference = preferenceRepository.save(
            Preference(
                meetingTeam = meetingTeam,
                filterCondition = "0000001",
                distanceCondition = "1000000",
            ),
        )

        entityManager.flush()
        entityManager.clear()

        preferenceUpdateDao.updatePreferenceByMeetingTeam(
            meetingTeam = meetingTeam,
            preferenceFilter = "1111110",
            preferenceDistance = "1111101",
        )

        // then
        val fixedMeetingTeam = preferenceRepository.findByMeetingTeam(meetingTeam)
        assertThat(fixedMeetingTeam).isNotNull
        assertThat(fixedMeetingTeam?.distanceCondition).isEqualTo("1111101")
        assertThat(fixedMeetingTeam?.filterCondition).isEqualTo("1111110")
    }

    @Test
    fun `upSert를 통해서 정상적으로 information이 없을 때 생성할 수 있다`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val preference = preferenceRepository.findByMeetingTeam(meetingTeam)
        meetingServiceUtils.preferenceUpSert(preference, meetingTeam, "0010000", "1000000")
        entityManager.flush()
        entityManager.clear()

        // then
        val preferenceFindByMeetingTeam = preferenceRepository.findByMeetingTeam(meetingTeam)
        assertThat(preferenceFindByMeetingTeam).isNotNull
        assertThat(preferenceFindByMeetingTeam?.distanceCondition).isEqualTo("0010000")
        assertThat(preferenceFindByMeetingTeam?.filterCondition).isEqualTo("1000000")
    }

    @Test
    fun `upSert를 통해서 정상적으로 information이 있을 때 업데이트할 수 있다`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val preference = preferenceRepository.save(
            Preference(
                meetingTeam = meetingTeam,
                filterCondition = "0000000",
                distanceCondition = "1000000",
            ),
        )
        meetingServiceUtils.preferenceUpSert(preference, meetingTeam, "0110000", "1000000")
        entityManager.flush()
        entityManager.clear()

        // then
        val informationFindByMeetingTeam = preferenceRepository.findByMeetingTeam(meetingTeam)
        assertThat(informationFindByMeetingTeam).isNotNull
        assertThat(informationFindByMeetingTeam?.filterCondition).isEqualTo("1000000")
        assertThat(informationFindByMeetingTeam?.distanceCondition).isEqualTo("0110000")
    }
}
