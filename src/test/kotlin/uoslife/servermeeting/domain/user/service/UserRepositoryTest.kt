package uoslife.servermeeting.domain.user.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
internal class UserRepositoryTest
@Autowired
constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val entityManager: EntityManager
) : BehaviorSpec() {

    override fun extensions() = listOf(SpringExtension)
    init {
        given("UserRepository") {
            `when`("save") {
                then("save") {
                    val user = userRepository.save(User(name = "test", email = "df@gmail.com"))
                    user.email shouldBe "df@gmail.com"
                }
            }
        }
    }
}
