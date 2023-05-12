package uoslife.servermeeting.match.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.match.entity.Compatibility

interface CompatibilityRepository : JpaRepository<Compatibility, Long> {

}