import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1,
    iterations: 5,
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    let flagsRes = http.get(`${BASE_URL}/flags`);
    check(flagsRes, {
        'flags status is 200': (r) => r.status === 200,
        'flags list includes homepage_banner': (r) => r.body.includes('"homepage_banner"'),
    });

    let evalRes = http.get(`${BASE_URL}/flags/homepage_banner/evaluate?userId=testuser&country=IE`);
    check(evalRes, {
        'evaluate status is 200': (r) => r.status === 200,
        'evaluate returns variant A': (r) => r.body.includes('"variant":"A"'),
    });

    sleep(1);
}
