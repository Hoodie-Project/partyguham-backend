import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 100, // 동시에 100명이 접속
  duration: '30s', // 30초 동안 지속
};

export default function () {
  // 1. 테스트할 API 주소 (로컬 서버 주소)
  const url = 'https://dev.partyguham.com/api/v2/users/me/profile';

  // 2. 만약 인증 토큰이 필요하다면 헤더 추가 (필요 없으면 생략 가능)
  const params = {
    headers: {
      'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1MiIsInJvbGUiOiJVU0VSIiwiaXNzIjoicGFydHlndWhhbSIsImlhdCI6MTc2OTUyNjQ2NCwiZXhwIjoxODAxMDYyNDY0fQ.vOh0a_qtfCc3-6IhrgKN0ouIpoafYa7s5raaw_7Sd18',
      'Content-Type': 'application/json',
    },
  };

  let res = http.get(url, params);

  // 3. 검증 (상태코드가 200인지, 응답 시간이 500ms 이내인지)
  check(res, {
    'is status 200': (r) => r.status === 200,
    // 'transaction time < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(0.1);
}