# raidho_BE
[raidho 사이트 바로가기 <클릭>](https://raidho.site/)
<br />
<b>전 세계 어디든 여행 함께해요!</b>
<br />
<img src="https://github.com/project-raidho/raidho_FE/blob/yoojin/src/assets/banner/raidhoIntro.png?raw=true" width="900">

## 프로젝트 소개

- 📌 전 세계 어디든 다양한 여행, 만남의 중심 라이도
- 📌 여행하고 싶은 사람들, 여행을 좋아하는 사람들이 이용하는 공간으로, 함께 여행하고 싶은 사람을 모집하고, 여행 경험을 공유하는 웹 커뮤니티 서비스
- 📌 일반적인 여행 뿐 아니라 태그 기능을 이용하여 자전거, 오토바이 등 특정 여행을 갈 사람들만 모집해 볼수있는 서비스

---

## 📆 프로젝트 기간

- 2022.08.26 - 2022.10.07

---

## 주요 기능

- 소셜로그인을 통한 간편한 회원가입
- 여행 후기 작성
- 원하는 비율과 원하는 사이즈로 이미지를 편집하는 기능
- 태그를 통한 검색 기능
- 여행 모집글 작성
- 실시간 소통을 할 수 있는 채팅 기능
- 화면 다크 & 라이트 모드

---

## 기술정보

<div align=center> 
      <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
      <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
      <img src="https://img.shields.io/badge/codedeploy-6DB33F?style=for-the-badge&logo=codedeploy&logoColor=white">
  <br>
      <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white">
      <img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=for-the-badge&logo=JSON Web Tokens&logoColor=white">   
      <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"> 
     <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 
  <br>
   <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=IntelliJ IDEA&logoColor=white">   
   <img src="https://img.shields.io/badge/Sourcetree-0052CC?style=for-the-badge&logo=Sourcetree&logoColor=white"> 
   <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white">
   <img src="https://img.shields.io/badge/Slack-4A154B7?style=for-the-badge&logo=Slack&logoColor=white">
   <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white">
   <br>
   <img src="https://img.shields.io/badge/AmazonEC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white">
   <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white"> 
   <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
   <img src="https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=Ubuntu&logoColor=white">
  <br>
   <img src="https://img.shields.io/badge/socket.io-010101?style=for-the-badge&logo=socket.io&logoColor=white">
   <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white">
   <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
   <img src="https://img.shields.io/badge/kakao login-FFCD00?style=for-the-badge&logo=kakao&logoColor=black">
</div>

### Spring boot

- 국내에서는 예전부터 현재까지 Back-end 언어로 JAVA가 많이 사용되고 있는 것으로 알고 있으며, Spring boot는 이전 버전들에 비해 구조가 간단하여 접근성이 좋다.

### JWT

- Session을 이용한 방식과 JWT 인증방식 두가지를 고민하였으며, 그 결과 Session방식에 비해 별도의 저장소가 필요없는 JWT방식이 서버자원 절약에 유리하다고 판단하여 적용하였음

### OAuth2

- 사용자 입장에서는 여러 서비스들을 하나의 계정으로 관리할 수 있게되어 편해지고 개발자 입장에서는 민감한 사용자 정보를 다루지 않아 위험부담이 줄고 서비스 제공자로부터 사용자 정보를 활용할 수 있다

### redis

- Message broker 역할로 사용
- 잦은 조회가 예상되는 자료를 저장하는 in memory cache로 사용

### mysql

- RDB 특성상 정해진 스키마에 따라 데이터를 명확하게 구분해서 저장해야 되기 때문에 데이터 구조 설계시 불필요한 데이터 중복과 잘못된 데이터 저장 작업을 줄일 수 있어 사용
- 현재 진행중인 프로젝트 규모가 크지 않아 여러 RDB중 mysql로도 충분한 커버가 가능할 것으로 판단

### S3

- 모든 미디어 파일을 한번에 관리할수 있다
- 인증시스템을 설정하여 보안이 좋다

### JPA

- 초기 개발 과정에서 비지니스로직 구성에 집중하기 위해 팀원들의 숙련도가 비교적 높은 JPA 사용
- N+1문제 해결과 조회 성능 향상을 위해 nativequery를 함께 적용

### WebSocket

- Http 통신은 클라이언트의 요청이 있을때만 서버가 응답하는 단방향 통신이여서 실시간 채팅 서비스 구현에 적절하지 못하기 때문에 양방향 통신을 지원하는 Socket 통신 방식을 적용하기 위해 WebSocket 프로토콜 사용

### Stomp

- Spring Security를 적용해 메세지 보호 가능
- WebSocket만 사용해서 구현하면 해당 메시지가 어떤 요청인지, 어떤 포맷으로 오는지 그리고 메시지 통신 과정을 어떻게 처리해야하는지 정해져 있지 않아 일일이 구현해야 한다. 이를 보완하기 위해 Stomp를 사용하여 메시지의 형식, 유형, 내용등을 정의할 수 있으며, 단순한 Binary, Text가 아닌 규격을 갖춘 메시지를 보낼수 있어 사용
- 메세지 브로커로 In Memory Broker를 사용하면 세션 수용 크기가 제한 되는등 단점이 있어, RabbitMQ, ActiveMQ등 전용 외부 브로커 사용이 가능하여 확장성이 좋은 STOMP를 사용

### SockJS

- websocket프로토콜을 지원하지 않는 브라우저에서 Http Streaming, Long-Polling 같은 Http 기반의 다른 기술로 전환해  연결하기 위해 사용

### EC2

- 이번 프로젝트는 수익성을 목표로한 프로젝트가 아니다보니 초기 투자비용이 발생가능한 부분을 배제하는 과정에서 프리티어를 제공하는 AWS의 EC2가 최적이라 판단되었고,  RDS, S3등 AWS 제공하는 서비스를 사용하기에 함께 관리하기에도 효율적이라 판단하여 사용
---

## ⚒️ ERD
<img src="https://github.com/project-raidho/raidho_FE/blob/yoojin/docs/erd.png?raw=true" width="900px" />

---

## Architecture
<img src="https://github.com/project-raidho/raidho_FE/blob/yoojin/docs/tech.png?raw=true" width="900px" />

---

## API 명세서
[API 명세서 바로가기 <클릭>](https://docs.google.com/spreadsheets/d/1yF2awtaIPyzk1X7WkjB2KvOp6AnWkhKdqcXWjgv-Pwc/edit#gid=908212804)

---

## ✍ 커밋 메세지 규칙

- feat : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- refactor : 코드 리팩토링
- test : 테스트 코드, 리팩토링 테스트 코드 추가
- chore : 빌드 업무 수정, 패키지 매니저 수정
- 제목은 50자 미만, 문장의 끝에 마침표 넣지 않음. 과거 시제 사용하지 않고, 명령어로 작성하도록  함.
- 제목 외에 추가적으로 정보를 전달하고 싶을 경우 본문에 추가 정보 기입
    - **예시 : [FEAT] comment  CRUD 기능 추가**

---

## 맴버 정보

| Position         | Name   | Blog                                                     | MBTI |
| ---------------- | ------ | -------------------------------------------------------- | ---- |
| 리더·FE·ReactJS  | 나유진 | 🔗 [GitHub::YooJinRa](https://github.com/YooJinRa)       | INFP |
| FE·ReactJS       | 김경문 | 🔗 [GitHub::rudans987](https://github.com/rudans987)     | INFJ |
| 부리더·BE·Spring | 박상욱 | 🔗 [GitHub::ParkRio](https://github.com/ParkRio/ParkRio) | ENFP |
| BE·Spring        | 김성호 | 🔗 [GitHub::kimsoungho](https://github.com/kimsoungho)   | INFP |
| BE·Spring        | 전태훈 | 🔗 [GitHub::JeonTaehun](https://github.com/JeonTaehun)   | INFJ |
| UX/UI            | 강예진 |                                                          | ENFP |

---