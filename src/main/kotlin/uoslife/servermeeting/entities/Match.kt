package uoslife.servermeeting.entities

import jakarta.persistence.*
@Entity
@Table(name = "matches")
data class Match(
    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val matchId: Int,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    val maleTeam: MeetingTeam,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    val femaleTeam: MeetingTeam
)
