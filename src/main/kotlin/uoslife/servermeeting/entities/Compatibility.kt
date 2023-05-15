package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "compatibility")
class Compatibility(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_team_id")
    val maleTeam: MeetingTeam? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "female_team_id")
    val femaleTeam: MeetingTeam? = null,
) : BaseEntity()
