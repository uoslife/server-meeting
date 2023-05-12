package uoslife.servermeeting.match.entity

import jakarta.persistence.*
import uoslife.servermeeting.meeting.entity.MeetingTeam

@Entity
@Table(name = "matches")
class Match(
    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val matchId: Int,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    val maleTeam: MeetingTeam,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    val femaleTeam: MeetingTeam,


    val matchProcess: MatchProcess,
)
