# server-meeting

서울시립대학교 학생들을 위한 미팅 서비스를 제공하는 프로젝트 시대팅의 서버 레포지토리입니다. (최신 버전: 5)

[![codecov](https://codecov.io/gh/uoslife/server-meeting/branch/main/graph/badge.svg?token=5ZBiZirJdI)](https://codecov.io/gh/uoslife/server-meeting)

- API Docs : [swagger](https://uosmeeting.uoslife.net/swagger-ui/swagger-ui/index.html#/)

## Environment

### requirements
- Kotlin & JDK 17
- Springboot 3.0.5

### installation

**설치**

```bash
$ git clone https://github.com/uoslife/server-meeting
```

**운영 파일 추가**
```bash
$ cd server-meeting && touch src/main/resources/application.yml
```

**빌드**

```bash
$ ./gradlew build testClasses -x test
```

**실행**
```bash
$ java -jar build/libs/server-meeting-0.0.1-SNAPSHOT.jar
```

## Infrastructure

EC2 & RDS 기준입니다. 본인이 실력이 있다면, 오토 스케일링, 온프레 등등 시도해보세요.
(다만 현재는 REDIS, ConcurrentMap을 사용하고 있습니다)

- EC2 접속 후, NGINX and SSL 설정 (to 8081 port)
- 도커, 도커 컴포즈 설치
- REDIS 설치 후, 비밀번호 설정 및 외부 바인드 오픈 (외부 확인용)
- RDS 생성
- sudo vi .env 를 통해 env 파일 환경변수 주입
- Git Action 환경변수 설정 (ec2, docker hub)
- Git Action 실행

## Env

- PortOne KEY -> 관리자 콘솔
- Cookie -> 프론트엔드 도메인 
- AWS KEY -> UOSLIFE AWS

## 알아둬야할 점

- 모든 주문정보는 SOFT DELETE 진행
- 주문 정보 생성, 팀 생성은 1초 이내 동시 요청 시 두 번째 요청은 거절
- 선호 테이블, 유저 Info 테이블에는 FK에 UNIQUE 제약 조건 직접 삽입 (JPA + NULL 정책 때문에 UNIQUE가 적용X)
- 가입, 로그인 플로우를 분리하는 것 고려
