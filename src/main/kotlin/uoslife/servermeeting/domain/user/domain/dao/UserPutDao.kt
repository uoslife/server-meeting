package uoslife.servermeeting.domain.user.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.QUser.user
import java.util.*

@Repository
class UserPutDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun putUser(existingUser: User) {
        queryFactory.update(user)
            .where(user.eq(existingUser))
            .setNull(user.birthYear)
            .setNull(user.department)
            .setNull(user.kakaoTalkId)
            .setNull(user.studentType)
            .setNull(user.smoking)
            .setNull(user.spiritAnimal)
            .setNull(user.mbti)
            .setNull(user.interest)
            .set(user.height, 0)
            .set(user.nickname, "user@" + UUID.randomUUID().toString())
            .execute()
    }
}
