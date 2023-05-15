package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "user_teams")
class UserTeam(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: MeetingTeam,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @Column(nullable = false)
    val isLeader: Boolean,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: TeamType? = null,
)
