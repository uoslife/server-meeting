package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.UUID
import org.springframework.stereotype.Repository
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserPutDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun putUser(existingUser: User) {
        queryFactory
            .update(user)
            .where(user.eq(existingUser))
            .set(user.name, "user@" + UUID.randomUUID().toString())
            .execute()
    }
}
