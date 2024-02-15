package uoslife.servermeeting.match.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository @Transactional class MatchedDao(private val queryFactory: JPAQueryFactory) {}
