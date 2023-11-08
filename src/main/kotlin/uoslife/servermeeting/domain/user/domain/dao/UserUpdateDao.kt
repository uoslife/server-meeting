package uoslife.servermeeting.domain.user.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.QUser.user

@Repository
class UserUpdateDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun updateUser(requestDto: UserUpdateRequest, existingUser: User) {
        queryFactory.update(user)
            .where(user.eq(existingUser))
            .set(user.nickname, requestDto.nickname)
            .execute()
    }
}
