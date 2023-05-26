package uoslife.servermeeting.domain.user.domain.common

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import uoslife.servermeeting.domain.user.domain.dao.UserUpdateDao
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import uoslife.servermeeting.domain.user.domain.service.UserService
import java.util.*

@SpringBootTest
@Transactional
abstract class UserServiceTest {
    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var entityManager: EntityManager

    val user1 = User(
        id = UUID.randomUUID(),
        name = "name1",
        nickname = "nickname1" ,
        phoneNumber = "01012345678",
        profilePicture = ""
    )

    val user2 = User(
        id = UUID.randomUUID(),
        name = "name2",
        nickname = "nickname2",
        phoneNumber = "01056781234",
        profilePicture = ""
    )

    @BeforeEach
    fun setUp() {
        userRepository.save(user1)
        userRepository.save(user2)
    }

}
