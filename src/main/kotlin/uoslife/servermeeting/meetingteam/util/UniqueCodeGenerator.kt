package uoslife.servermeeting.meetingteam.util

import java.security.SecureRandom
import org.springframework.stereotype.Component
import uoslife.servermeeting.verification.repository.VerificationRedisRepository
import uoslife.servermeeting.meetingteam.exception.TeamCodeGenerateFailedException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository

@Component
class UniqueCodeGenerator(
    private val meetingTeamRepository: MeetingTeamRepository,
    private val verificationRedisRepository: VerificationRedisRepository,
) {
    fun getUniqueTeamCode(): String {
        val characters = ('A'..'Z') + ('0'..'9') // A-Z, 0-9 문자열 리스트
        val random = SecureRandom.getInstanceStrong()

        var code: String
        var attempts = 0
        var isDuplicate: Boolean = true
        do {
            // 난수 생성
            code =
                (1..4)
                    .map { characters[random.nextInt(characters.size)] } // 리스트에서 임의로 선택
                    .joinToString("") // 선택된 문자들을 연결하여 문자열 생성

            // DB 내 코드의 중복 체크
            isDuplicate = meetingTeamRepository.existsByCode(code)

            if (isDuplicate) {
                attempts++
            }
        } while (isDuplicate && attempts < 3) // 중복일 경우 최대 3번까지 재생성 및 검증

        if (isDuplicate) {
            throw TeamCodeGenerateFailedException()
        }

        return code
    }

    fun getUniqueCertCode(): String {
        val characters: List<Char> = ('0'..'9').toList() // A-Z, 0-9 문자열 리스트
        val random: SecureRandom = SecureRandom.getInstanceStrong()

        var code: String = (1..6)
            .map { characters[random.nextInt(characters.size)] }
            .joinToString { "" }

        return code
    }
}
