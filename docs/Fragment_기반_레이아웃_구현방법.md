# 🔧 Fragment 기반 레이아웃 구현 방법 (구체적 설명)

## 📋 현재 문제점

### 중복되는 코드

**home.html, detail.html, search.html 모두에 반복되는 부분:**

```html
<!-- 모든 페이지에 중복 -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>...</title>  <!-- 제목만 다름 -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdn.jsdelivr.net/npm/daisyui@4.12.10/dist/full.min.css" rel="stylesheet" />
    <link rel="stylesheet" th:href="@{/css/main.css}">
    <!-- 페이지별 CSS는 여기 추가 -->
</head>
<body class="min-h-screen flex flex-col">
    <header th:replace="~{fragments/components/header :: site-header}"></header>
    
    <main class="flex-grow">
        <!-- 여기만 각 페이지마다 다름 -->
    </main>
    
    <footer th:replace="~{fragments/components/footer :: site-footer}"></footer>
    <script th:src="@{/js/app.js}" type="module" defer></script>
</body>
</html>
```

---

## 💡 해결 방법: 공통 레이아웃 Fragment 생성

### 1단계: 공통 레이아웃 Fragment 만들기

**`fragments/layout/base-layout.html`** 파일 생성

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:fragment="base-layout(title, additionalStyles, mainContent)">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${title}">오늘의영화</title>
    
    <!-- Google Analytics (공통) -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=G-13180281396"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());
        gtag('config', 'G-PSBJDPBE30');
    </script>
    
    <!-- 공통 CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdn.jsdelivr.net/npm/daisyui@4.12.10/dist/full.min.css" rel="stylesheet" />
    <link rel="stylesheet" th:href="@{/css/main.css}">
    
    <!-- 페이지별 추가 CSS -->
    <th:block th:if="${additionalStyles != null}">
        <th:block th:each="style : ${additionalStyles}">
            <link rel="stylesheet" th:href="@{${style}}">
        </th:block>
    </th:block>
</head>
<body class="min-h-screen flex flex-col">
    <!-- 공통 헤더 -->
    <header th:replace="~{fragments/components/header :: site-header}"></header>
    
    <!-- 메인 콘텐츠 (각 페이지마다 다름) -->
    <main class="flex-grow">
        <th:block th:replace="${mainContent}"></th:block>
    </main>
    
    <!-- 공통 푸터 -->
    <footer th:replace="~{fragments/components/footer :: site-footer}"></footer>
    
    <!-- 공통 JavaScript -->
    <script th:src="@{/js/app.js}" type="module" defer></script>
</body>
</html>
```

---

### 2단계: 각 페이지에서 레이아웃 Fragment 사용

#### home.html (개선 전 → 개선 후)

**개선 전:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>오늘의영화</title>
    <!-- ... 모든 head 내용 ... -->
</head>
<body>
    <!-- ... 모든 body 내용 ... -->
</body>
</html>
```

**개선 후:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout/base-layout :: base-layout(
        '홈 - 오늘의영화',
        ${['/css/sections/boxoffice.css', '/css/sections/showtime.css', '/css/sections/cinema.css']},
        ~{::content}
      )}">
<th:block th:fragment="content">
    <div class="max-w-6xl mx-auto px-4">
        <section id="boxoffice" class="page-section"
                 th:insert="~{fragments/sections/boxoffice :: boxoffice}"></section>
        <section class="page-section"
                 th:insert="~{fragments/sections/cinema :: cinema}"></section>
    </div>
</th:block>
</html>
```

#### detail.html (개선 전 → 개선 후)

**개선 전:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title th:text="${movie.title} + ' - 오늘의영화'">영화 상세</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- ... -->
</head>
<body>
    <!-- ... -->
</body>
</html>
```

**개선 후:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout/base-layout :: base-layout(
        ${movie != null ? movie.title + ' - 오늘의영화' : '영화 상세 - 오늘의영화'},
        ${null},
        ~{::content}
      )}">
<th:block th:fragment="content">
    <div class="max-w-4xl mx-auto px-4 py-8">
        <!-- 홈으로 돌아가기 버튼 -->
        <div class="mb-6">
            <a href="/" class="inline-flex items-center text-blue-600 hover:text-blue-800">
                <!-- ... -->
            </a>
        </div>
        
        <!-- 영화 상세 정보 카드 -->
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <!-- ... 영화 정보 ... -->
        </div>
    </div>
</th:block>
</html>
```

#### search.html (개선 전 → 개선 후)

**개선 후:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout/base-layout :: base-layout(
        ${keyword != null ? keyword + ' 검색 결과 - 오늘의영화' : '영화 검색 - 오늘의영화'},
        ${null},
        ~{::content}
      )}">
<th:block th:fragment="content">
    <div class="max-w-6xl mx-auto px-4 py-8">
        <!-- 검색 결과 내용 -->
        <!-- ... -->
    </div>
</th:block>
</html>
```

---

## 🎯 핵심 개념 설명

### "레이아웃은 하나"인데 Fragment로 어떻게?

**답변:**
1. **공통 레이아웃을 Fragment로 만들기**
   - `base-layout.html`에 전체 HTML 구조를 Fragment로 정의
   - 이 Fragment가 "하나의 통일된 레이아웃"

2. **각 페이지는 그 Fragment를 사용**
   - `th:replace`로 전체 페이지를 Fragment로 교체
   - 페이지별로 다른 부분만 파라미터로 전달

3. **결과**
   - 모든 페이지가 동일한 레이아웃 구조 사용
   - 중복 코드 제거
   - 레이아웃 변경 시 한 곳만 수정

---

## 📊 비교: 개선 전 vs 개선 후

### 개선 전
```
home.html      → 전체 HTML 구조 (100줄)
detail.html    → 전체 HTML 구조 (117줄)
search.html    → 전체 HTML 구조 (105줄)
─────────────────────────────────────
총 322줄, 중복 코드 많음
```

### 개선 후
```
base-layout.html → 공통 레이아웃 (50줄)
home.html        → 콘텐츠만 (15줄)
detail.html      → 콘텐츠만 (30줄)
search.html      → 콘텐츠만 (20줄)
─────────────────────────────────────
총 115줄, 중복 코드 없음
```

---

## 🔍 작동 원리

### 1. Fragment 정의
```html
<!-- fragments/layout/base-layout.html -->
<html th:fragment="base-layout(title, additionalStyles, mainContent)">
  <!-- 공통 구조 -->
</html>
```

### 2. Fragment 사용
```html
<!-- home.html -->
<html th:replace="~{fragments/layout/base-layout :: base-layout(...)}">
  <!-- 이 부분이 mainContent로 전달됨 -->
</html>
```

### 3. Thymeleaf 처리 과정
1. `th:replace`가 `base-layout` Fragment를 찾음
2. 파라미터 전달: `title`, `additionalStyles`, `mainContent`
3. Fragment 내부에서 `${mainContent}`를 `th:replace`로 교체
4. 최종 HTML 생성

---

## ✅ 장점

1. **중복 제거**: 공통 코드를 한 곳에만 작성
2. **일관성**: 모든 페이지가 동일한 레이아웃 사용
3. **유지보수**: 레이아웃 변경 시 한 파일만 수정
4. **유연성**: 페이지별로 다른 CSS, 제목 등 쉽게 지정
5. **의존성 없음**: 추가 라이브러리 불필요

---

## 🚀 실제 구현 예시

위의 방법대로 구현하면:

1. **공통 레이아웃 파일 1개** 생성
2. **각 페이지를 간단하게** 변경
3. **모든 페이지가 통일된 레이아웃** 사용

이것이 "Fragment 기반으로 레이아웃을 하나로 통일"하는 방법입니다!

---

**작성일**: 2025-01-XX  
**버전**: 1.0



