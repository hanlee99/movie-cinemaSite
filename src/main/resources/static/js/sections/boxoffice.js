export function initBoxoffice() {
  const track = document.getElementById("movieTrack");
  const prevBtn = document.getElementById("prevBtn");
  const nextBtn = document.getElementById("nextBtn");
  const title = document.getElementById("title");
  const showRange = document.getElementById("showRange");
  const slideViewBtn = document.getElementById("slideViewBtn");
  const listViewBtn = document.getElementById("listViewBtn");
  const toggleBtn = document.getElementById("toggleViewBtn");

  let isListView = false;
  const { daily, nowPlaying, upcoming } = window.boxofficeData;
  let currentSlide = 0;
  let slides = [];
  let movies = [];
  let len = 4;
  let currentType = "daily";

  track.style.display = "flex";
  track.style.gap = "16px";
  track.style.transition = "transform 0.5s ease";

  // 슬라이드 그룹 만들기
  function makeSlides(total) {
    const result = [];
    const lastStart = Math.max(0, total - len);

    for (let i = 0; i < lastStart; i += len) {
      result.push([i, i + 1, i + 2, i + 3]);
    }

    // 마지막 그룹
    if (total > len) {
      const last = [];
      for (let i = lastStart; i < total; i++) last.push(i);
      result.push(last);
    } else {
      const all = [];
      for (let i = 0; i < total; i++) all.push(i);
      result.push(all);
    }

    return result;
  }

  function renderCards() {
    track.innerHTML = "";

    movies.forEach((m, i) => {
      const card = document.createElement("div");
      card.className = "card";
      card.dataset.index = i;

      const figure = document.createElement("figure");
      const img = document.createElement("img");

      const link = document.createElement("a");  // ✅ 추가
      link.href = `/movies/${m.id}`;              // ✅ 추가

      img.src = m.poster || (m.posters?.split("|")[0]) || "/images/irani12.png";
      img.alt = m.title;
      link.appendChild(img);                      // ✅ 변경 (figure → link)
      figure.appendChild(link);                   // ✅ 변경 (img → link)

      const body = document.createElement("div");
      body.className = "card-body p-4 text-center";

      const h2 = document.createElement("h2");
      h2.className = "card-title text-base font-semibold justify-center";
      const titleLink = document.createElement("a");
      titleLink.href = `/movies/${m.id}`;
      titleLink.textContent = m.title;
      titleLink.className = "hover:text-blue-600 transition";
      h2.appendChild(titleLink);

      const date = document.createElement("p");
      date.className = "text-gray-500 text-sm";
      //date.textContent = `${formatDate(m.openDt || m.repRlsDate)} 개봉`;
      date.textContent = `${m.openDt || m.repRlsDate || ""} 개봉`;

      body.append(h2, date);
      card.append(figure, body);

      track.appendChild(card);
    });

    setTimeout(() => updateSlide(false), 30);
  }

  function updateSlide(animate = true) {
    const cards = track.querySelectorAll(".card");
    if (!cards.length) return;

    const cardWidth = cards[0].offsetWidth;
    const offset = slides[currentSlide][0] * (cardWidth + 16);

    track.style.transition = animate ? "transform 0.5s ease" : "none";
    track.style.transform = `translateX(-${offset}px)`;
  }

  function move(dir) {
    currentSlide = (currentSlide + dir + slides.length) % slides.length;
    updateSlide(true);
  }

  function switchTo(type, data, label, isList) {
    currentType = type;
    currentSlide = 0;
    if (!data) {
            console.error(`${type} 데이터가 없습니다`);
            movies = [];
        } else if (type === "daily") {
            // daily는 { movies: [...] } 구조
            movies = Array.isArray(data.movies) ? data.movies : [];
        } else {
            // nowPlaying, upcoming은 { content: [...] } 구조
            movies = Array.isArray(data.content) ? data.content : [];
        }
    slides = makeSlides(movies.length);

    title.textContent = label;

    if (type === "daily") {
      showRange.textContent = data.showRange || data.range || "";
    } else {
      showRange.textContent = new Date().toISOString().slice(0, 10).replace(/-/g, "");
    }

    renderCards();

    if (isListView) {
      prevBtn.style.display = "none";
      nextBtn.style.display = "none";
    } else {
      prevBtn.style.display = movies.length > len ? "flex" : "none";
      nextBtn.style.display = movies.length > len ? "flex" : "none";
    }

    Object.values(btns).forEach(b => b?.classList.remove("bg-blue-50"));
    btns[type]?.classList.add("bg-blue-50");

    updateToggleBtn(type);
  }

  const btns = {
    daily: document.getElementById("btnDaily"),
    now: document.getElementById("btnNow"),
    upcoming: document.getElementById("btnUpcoming"),
  };

  btns.daily?.addEventListener("click", () => switchTo("daily", daily, "일간 박스오피스", false));
  btns.now?.addEventListener("click", () => switchTo("now", nowPlaying, "최근 개봉 영화", true));
  btns.upcoming?.addEventListener("click", () => switchTo("upcoming", upcoming, "상영 예정 영화", true));

  nextBtn?.addEventListener("click", () => move(1));
  prevBtn?.addEventListener("click", () => move(-1));

  switchTo("daily", daily, "일간 박스오피스", false);

  listViewBtn?.addEventListener("click", () => {
    isListView = true;
    track.style.transition = "none";
    track.style.transform = "translateX(0)";
    track.style.flexWrap = "wrap";
    track.style.justifyContent = "center";
    prevBtn.style.display = "none";
    nextBtn.style.display = "none";

    listViewBtn.classList.add("bg-blue-100");
    slideViewBtn.classList.remove("bg-blue-100");

    updateToggleBtn(currentType);
  });

  slideViewBtn?.addEventListener("click", () => {
    isListView = false;
    track.style.flexWrap = "nowrap";
    track.style.justifyContent = "flex-start";
    prevBtn.style.display = movies.length > len ? "flex" : "none";
    nextBtn.style.display = movies.length > len ? "flex" : "none";

    updateSlide(false);

    slideViewBtn.classList.add("bg-blue-100");
    listViewBtn.classList.remove("bg-blue-100");

    updateToggleBtn(currentType);
  });

  function updateToggleBtn(type) {
    if ((type === "now" || type === "upcoming") && isListView) {
      toggleBtn.classList.remove("hidden");
    } else {
      toggleBtn.classList.add("hidden");
    }
  }

  let currentPage = 0;
  const pageSize = 20;
  const maxCount = 150;
  let totalLoaded = 0;

  toggleBtn?.addEventListener("click", async () => {
      prevBtn.disabled = true;
      nextBtn.disabled = true;
      slideViewBtn.disabled = true;
      listViewBtn.disabled = true;
      Object.values(btns).forEach(b => (b.disabled = true));

      if (totalLoaded >= maxCount) {
        toggleBtn.textContent = "마지막 페이지입니다";
        toggleBtn.disabled = true;

        prevBtn.disabled = false;
        nextBtn.disabled = false;
        slideViewBtn.disabled = false;
        listViewBtn.disabled = false;
        Object.values(btns).forEach(b => (b.disabled = false));
        return;
      }

      currentPage++;
      const endpoint = currentType === "now" ? "/movie/now-playing" : "/movie/upcoming";
      const url = `${endpoint}?page=${currentPage}&size=${pageSize}`;

      try {
        const res = await fetch(url);
        const newMovies = await res.json();

        if (!Array.isArray(newMovies) || newMovies.length === 0) {
          toggleBtn.textContent = "더 이상 데이터 없음";
          toggleBtn.disabled = true;
          return;
        }

        totalLoaded += newMovies.length;

        newMovies.forEach((m) => {
          // ... 카드 추가 로직 ...
        });

      } catch (err) {
        console.error("더보기 요청 실패:", err);
        toggleBtn.classList.add("hidden");
      } finally {
        prevBtn.disabled = false;
        nextBtn.disabled = false;
        slideViewBtn.disabled = false;
        listViewBtn.disabled = false;
        Object.values(btns).forEach(b => (b.disabled = false));
      }
  });
}
