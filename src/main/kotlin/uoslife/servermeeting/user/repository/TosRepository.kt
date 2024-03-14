package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.Tos

interface TosRepository: JpaRepository<Tos, Long>{

}
