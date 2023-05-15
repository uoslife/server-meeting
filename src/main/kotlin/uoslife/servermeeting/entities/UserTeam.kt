package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "user_teams")
class UserTeam(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: MeetingTeam,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,

    @Column(nullable = false)
    var isLeader: Boolean,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: TeamType? = null,
)
