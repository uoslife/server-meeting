package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "match")
class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_team_id")
    val maleTeam: MeetingTeam,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "female_team_id")
    val femaleTeam: MeetingTeam,
) : BaseEntity()
