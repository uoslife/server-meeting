package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "compatibility")
class Compatibility(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    var score: Int? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_team_id")
    var maleTeam: MeetingTeam? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "female_team_id")
    var femaleTeam: MeetingTeam? = null,
) : BaseEntity()
