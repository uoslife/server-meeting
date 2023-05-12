package uoslife.servermeeting.meeting.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meeting.entity.Preference

interface PreferenceRepository : JpaRepository<Preference, Long> {

}