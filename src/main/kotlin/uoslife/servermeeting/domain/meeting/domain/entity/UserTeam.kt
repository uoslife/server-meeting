package uoslife.servermeeting.domain.meeting.domain.entity

import jakarta.persistence.*
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.User

@Entity
@Table(name = "user_team")
class UserTeam(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: MeetingTeam,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,

    @Column(nullable = false)
    var isLeader: Boolean,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: TeamType,

    @OneToOne(mappedBy = "userTeam")
    @PrimaryKeyJoinColumn
    var payment: Payment? = null,
) {
    companion object {
        fun createUserTeam(meetingTeam: MeetingTeam, user: User, isLeader: Boolean, teamType: TeamType): UserTeam {
            return UserTeam(
                team = meetingTeam,
                user = user,
                isLeader = isLeader,
                type = teamType
            )
        }
    }
}
