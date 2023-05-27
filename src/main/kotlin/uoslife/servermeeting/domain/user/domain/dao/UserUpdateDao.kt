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
    fun updateUser(requestDto: UserUpdateRequest, existingUser: User) {
        queryFactory.update(user)
            .where(user.eq(existingUser))
            .set(user.birthYear, requestDto.birthYear)
            .set(user.gender, requestDto.gender)
            .set(user.name, requestDto.name)
            .set(user.department, requestDto.department)
            .set(user.studentType, requestDto.studentType)
            .set(user.smoking, requestDto.smoking)
            .set(user.spiritAnimal, requestDto.spiritAnimal)
            .set(user.mbti, requestDto.mbti)
            .set(user.interest, requestDto.interest)
            .set(user.height, requestDto.height)
            .set(user.nickname, requestDto.nickname)
            .execute()
    }
}
