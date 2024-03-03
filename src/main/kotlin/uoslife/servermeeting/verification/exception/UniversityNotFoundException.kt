package uoslife.servermeeting.verification.exception

import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode
import uoslife.servermeeting.global.error.exception.InvalidValueException

class UniversityNotFoundException: InvalidValueException(ErrorCode.UNIVIERSITY_NOT_FOUND) {
}
