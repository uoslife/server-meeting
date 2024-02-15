package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserUpdateDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun updateUser(requestDto: UserUpdateRequest, existingUser: User) {
        queryFactory
            .update(user)
            .where(user.eq(existingUser))
            .set(user.nickname, requestDto.nickname)
            .execute()
    }
}
