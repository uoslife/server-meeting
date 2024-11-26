package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.user.entity.enums.*

@Entity
class UserInformation( // 시즌 정책에 따라 바뀌는 컬럼들
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @OneToOne @JoinColumn(nullable = false, name = "user_id") var user: User,
    @Enumerated(EnumType.STRING) var smoking: SmokingType? = null,
    var mbti: String? = null,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "interest_type", joinColumns = [JoinColumn(name = "user_info_id")])
    @Enumerated(EnumType.STRING)
    var interest: List<String>? = null, // 취미
    @Enumerated(EnumType.STRING) var appearanceType: AppearanceType? = null,
    @Enumerated(EnumType.STRING) var eyelidType: EyelidType? = null,
    var height: Int? = null,
    var age: Int? = null,
    var department: String? = null,
    var studentNumber: Int? = null,
    @Enumerated(EnumType.STRING) var studentType: StudentType? = null,
/* 시즌 5에선 제외된 컬럼입니다.
var studentType: StudentType = StudentType.UNDERGRADUATE,
var religion: ReligionType? = null,
var university: University? = null,
var drinkingMin: Int? = null,
var drinkingMax: Int? = null,
 */
)
