package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.Report

interface ReportRepository : JpaRepository<Report, Long> {

}