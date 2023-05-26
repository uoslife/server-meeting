package uoslife.servermeeting.domain.user.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.QUser.user

@Repository
@Transactional
class UserUpdateDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun updateUser(existingUser: User, requestDto: UserUpdateRequest) {
        existingUser.let {
            if (requestDto.hasChanges()) {
                val user = user
                queryFactory.update(user)
                    .where(user.id.eq(existingUser.id))
                    .apply {
                        requestDto.birthYear?.let { set(user.birthYear, it) }
                        requestDto.gender?.let { set(user.gender, it) }
                        requestDto.name?.let { set(user.name, it) }
                        requestDto.department?.let { set(user.department, it) }
                        requestDto.studentType?.let { set(user.studentType, it) }
                        requestDto.smoking?.let { set(user.smoking, it) }
                        requestDto.spiritAnimal?.let { set(user.spiritAnimal, it) }
                        requestDto.mbti?.let { set(user.mbti, it) }
                        requestDto.interest?.let { set(user.interest, it) }
                        requestDto.height?.let { set(user.height, it) }
                        requestDto.nickname?.let { set(user.nickname, it) }
                    }
                    .execute()
            }
        }
    }
}
