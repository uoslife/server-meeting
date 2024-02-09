package uoslife.servermeeting.domain.meeting.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.User

@Entity
@Table(name = "user_team")
class UserTeam(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: MeetingTeam,
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,
    @Column(nullable = false) var isLeader: Boolean,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var type: TeamType,
) {
    companion object {
        fun createUserTeam(
            meetingTeam: MeetingTeam,
            user: User,
            isLeader: Boolean,
            teamType: TeamType
        ): UserTeam {
            return UserTeam(team = meetingTeam, user = user, isLeader = isLeader, type = teamType)
        }
    }
}
