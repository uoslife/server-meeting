package uoslife.servermeeting.global.aop

import kotlin.annotation.AnnotationTarget.*

@Target(ANNOTATION_CLASS, FUNCTION) // 어노테이션이 적용될 위치를 지정
@Retention(AnnotationRetention.RUNTIME) // 런타임 시점에 유지되도록 설정
annotation class PreventDuplicateRequest
