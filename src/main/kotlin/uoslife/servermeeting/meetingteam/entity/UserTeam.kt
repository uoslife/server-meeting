package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "user_team")
class UserTeam(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long?=null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: MeetingTeam,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false)
    var isLeader: Boolean,

    ) {
    companion object {
        fun createUserTeam(meetingTeam: MeetingTeam, user: User, isLeader: Boolean) : UserTeam {
            return UserTeam(
                team = meetingTeam,
                user = user,
                isLeader = isLeader,
            )
        }
    }
}
