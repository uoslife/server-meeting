# server-meeting

서울시립대학교 학생들을 위한 미팅 서비스를 제공하는 프로젝트 시대팅의 서버 레포지토리입니다.

[![codecov](https://codecov.io/gh/uoslife/server-meeting/branch/main/graph/badge.svg?token=5ZBiZirJdI)](https://codecov.io/gh/uoslife/server-meeting)

- API Docs : [swagger](https://meeting.uoslife.com/api/swagger-ui/index.html)

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

- application.yml 파일은 notion에서 관리합니다.
- season 값은 application.yml에서 관리합니다.
- Production Database에 접근하기 위해서는 VPN을 사용해야 합니다.
