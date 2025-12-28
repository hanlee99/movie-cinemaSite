#!/bin/bash

# 동시성 테스트 스크립트
# 10개 요청을 동시에 전송

echo "=== 동시성 테스트 시작 ==="
echo "10개 요청을 동시에 전송합니다..."

# 백그라운드로 동시 실행 (&)
for i in {1..10}; do
    curl -s http://localhost:8080/movies/1 > /dev/null &
    echo "요청 $i 전송"
done

# 모든 요청 완료 대기
wait

echo ""
echo "=== 모든 요청 완료 ==="
echo "DB를 확인하세요:"
echo "SELECT view_count FROM movie_stats WHERE movie_id = 1;"
echo "예상 결과: view_count = 10"
