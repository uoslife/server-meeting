package uoslife.servermeeting.meeting.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meeting.entity.Information

interface InformationRepository : JpaRepository<Information, Long> {


}