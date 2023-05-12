package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.NoPreferDepartment

interface NoPreferDepartmentRepository : JpaRepository<NoPreferDepartment, Long> {

}
