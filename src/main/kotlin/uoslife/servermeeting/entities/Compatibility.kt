package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "compatibilities")
class Compatibility(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    val maleTeam: MeetingTeam,

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    val femaleTeam: MeetingTeam,
) : BaseEntity() {
    override fun toString(): String {
        return "Compatibility(id=$id, maleTeam=$maleTeam, femaleTeam=$femaleTeam)"
    }
}
