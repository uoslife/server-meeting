package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "user_teams")
data class UserTeam(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    val id: Long? = null,

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