package uoslife.servermeeting.meeting.entity

import jakarta.persistence.*
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "user_teams")
class UserTeam(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id", nullable = false, updatable = false)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "team_id", nullable = false)
    val team: MeetingTeam,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "is_leader", nullable = false)
    val isLeader: Boolean,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: TeamType
)