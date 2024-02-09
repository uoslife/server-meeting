package uoslife.servermeeting.domain.user.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.user.domain.entity.QUser.user
import uoslife.servermeeting.domain.user.domain.entity.User
import java.util.UUID

@Repository
class UserPutDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun putUser(existingUser: User) {
        queryFactory
            .update(user)
            .where(user.eq(existingUser))
            .set(user.nickname, "user@" + UUID.randomUUID().toString())
            .execute()
    }
}
