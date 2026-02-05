<img width="546" height="203" alt="logo2_primary" src="https://github.com/user-attachments/assets/4e60f630-d213-4bbb-8cc8-f0cf3d86579e" />

# 파티구함

💡 Background & Problem (문제 정의)
팀원 모집 서비스들의 한계.

- 정보의 비대칭성: 포지션과 기술 스택 위주의 형식적인 소개로 인해 실제 협업 성향(Soft Skill) 파악 불가.

- 비효율적인 검증 과정: 팀원을 찾기 위해 매번 긴 면접과 대화 과정을 거쳐야 하며, 이는 시간과 비용의 낭비로 이어짐.

- 불확실한 매칭: 팀원 간의 성향 불일치로 프로젝트가 중도 무산되는 경우가 빈번함.

🎯 Solution (해결 방안)
게임의 시스템(Gamification)을 도입하여 이 문제를 해결합니다.

1. 캐릭터 프로필 (User Stats & Tendency)
단순 자기소개 대신, 유저의 협업 스타일을 게임 능력치처럼 시각화합니다.
MBTI 및 작업 스타일(몰아서 하기 vs 꾸준히 하기)을 태그화하여 한눈에 파악.

2. 파티 모집 시스템 (Party Recruitment)
로비(Lobby): 프로젝트 모집글은 하나의 '던전 퀘스트'가 됩니다.
파티장 권한: 지원자의 '모험가 카드(프로필)'를 확인하고 클릭 한 번으로 수락/거절 결정. 불필요한 초기 대화 단축.
클리어 레코드: 이전 프로젝트 수행 이력을 데이터화하여 '숙련도'를 증명.

3. 게이미피케이션 (Gamification)
서비스 체류 시간을 높이기 위해 활동에 따른 경험치, 레벨업, 업적 시스템 도입.
딱딱한 구인구직이 아닌, 나만의 캐릭터를 성장시키는 즐거움 부여.

🚀 Expectation (기대 효과)

매칭 속도 향상: 시각화된 정보를 통해 면접 없이도 나와 잘 맞는 팀원을 빠르게 식별.
협업 성공률 증대: 실력뿐만 아니라 '성향' 기반 매칭으로 팀 내 불화 방지.
높은 리텐션: 게임 요소(레벨, 보상)를 통해 유저가 지속적으로 자신의 커리어를 관리하도록 유도

## 🛠 Tech Stack
<img width="6656" height="2654" alt="Partyguham System Architecture" src="https://github.com/user-attachments/assets/1822eef1-f439-4519-99e3-3537c081f11e" />

- Back-end: Java 17, Spring Boot 3.x, Spring Data JPA

- Database: PostgreSQL, HikariCP, Redis

- Infrastructure: On-premise(Mac mini), Nginx, Docker(vCPU 2-Core, Memory 2GB), Git Actions

- Test & Monitoring: k6, Prometheus, Grafana


## 백엔드 구현

### 👤 User & Auth
- **Social Login (OAuth 2.0)**: Google, Kakao 소셜 로그인 연동
- **Security**: JWT(Access/Refresh Token) 기반 인증 및 Spring Security를 통한 API 권한 제어

### ⚡ Performance & Database
- **Caching**: Redis 기반 데이터 캐싱으로 조회 병목 해소
- **Persistence**: JPA 연관관계 최적화 및 페이징 처리를 통한 데이터 처리 효율화
- **Optimizing**: 부하 테스트를 통한 HikariCP 및 Thread Pool 최적 설정 도출 (Error Rate 0%)

### 🪛 Tech Details
- **Exception Handling**: 공통 Response 객체 및 전역 예외 처리기 구현
- **Monitoring**: Grafana/Prometheus를 이용한 실시간 리소스 모니터링 대시보드 구축
