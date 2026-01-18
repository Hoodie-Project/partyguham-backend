import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 20 }, // 10초 동안 사용자를 20명까지 서서히 늘림 (Ramp-up)
    { duration: '20s', target: 20 }, // 20초 동안 20명 유지
    { duration: '10s', target: 0 },  // 10초 동안 사용자 0명으로 줄임 (Ramp-down)
  ],
};

export default function () {
  // 1. 테스트할 API 주소 (로컬 서버 주소)
  const url = 'http://localhost:8080/api/parties/1/members';

  // 2. 만약 인증 토큰이 필요하다면 헤더 추가 (필요 없으면 생략 가능)
  const params = {
    headers: {
      'Authorization': 'Bearer YOUR_TEST_TOKEN',
      'Content-Type': 'application/json',
    },
  };

  let res = http.get(url, params);

  // 3. 검증 (상태코드가 200인지, 응답 시간이 500ms 이내인지)
  check(res, {
    'is status 200': (r) => r.status === 200,
    'transaction time < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(1); // 실제 유저처럼 1초 대기
}