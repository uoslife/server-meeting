package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.Department

interface DepartmentRepository : JpaRepository<Department, Long> {
    fun findByName(name: String): Department?
}
