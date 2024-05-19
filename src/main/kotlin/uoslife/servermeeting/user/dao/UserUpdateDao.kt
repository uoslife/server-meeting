package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.UserPersonalInformation
import uoslife.servermeeting.user.entity.User

@Repository
class UserUpdateDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun updateUser(
        name: String,
        phoneNumber: String,
        kakaoTalkId: String,
        userPersonalInformation: UserPersonalInformation,
        existingUser: User
    ) {
        queryFactory
            .update(user)
            .where(user.eq(existingUser))
            .set(user.name, name)
            .set(user.phoneNumber, phoneNumber)
            .set(user.kakaoTalkId, kakaoTalkId)
            .set(user.userPersonalInformation, userPersonalInformation)
            .execute()
    }
}
