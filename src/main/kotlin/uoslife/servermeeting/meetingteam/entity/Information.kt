package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import java.io.Serializable
import uoslife.servermeeting.match.dto.response.MatchedInformation
import uoslife.servermeeting.user.entity.enums.GenderType

@Entity
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?= null,

    @OneToOne
    @JoinColumn(name="meeting_team_id")
    var meetingTeam: MeetingTeam?=null,

    @Enumerated(value = EnumType.STRING)
    val gender: GenderType? = null,
) {
    fun toMatchedInformation(): MatchedInformation {
        return MatchedInformation(gender = gender)
    }
}
