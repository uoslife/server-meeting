package uoslife.servermeeting.global.exception

data class CustomException( val errorCode : ErrorCode ) : RuntimeException() {

}