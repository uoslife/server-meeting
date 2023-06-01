package uoslife.servermeeting.domain.match.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.domain.match.domain.entity.Compatibility

interface CompatibilityRepository : JpaRepository<Compatibility, Long>
