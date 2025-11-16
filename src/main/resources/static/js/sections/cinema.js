export function initCinema() {
  const chips = document.querySelectorAll('.chip');

  let filters = {
    brand: 'all',
    region: 'all',
    special: 'all'
  };

  chips.forEach(chip => {
    chip.addEventListener('click', () => {
      const filterType = chip.dataset.brand ? 'brand' :
                        chip.dataset.region ? 'region' : 'special';

      // 같은 그룹 버튼 비활성화
      chip.parentElement.querySelectorAll('.chip').forEach(c =>
        c.classList.remove('active')
      );

      // 클릭한 버튼 활성화
      chip.classList.add('active');

      // 필터 적용
      filters[filterType] = chip.dataset.brand ||
                           chip.dataset.region ||
                           chip.dataset.special;

      filterTheaters(filters);
    });
  });
}

function filterTheaters(filters) {
  // 극장 필터링 로직
  const theaters = document.querySelectorAll('.theater-card');

  theaters.forEach(theater => {
    const brand = theater.dataset.brand;
    const region = theater.dataset.region;
    const special = theater.dataset.special;

    const showBrand = filters.brand === 'all' || brand === filters.brand;
    const showRegion = filters.region === 'all' || region === filters.region;
    const showSpecial = filters.special === 'all' ||
                       special?.includes(filters.special);

    if (showBrand && showRegion && showSpecial) {
      theater.style.display = 'block';
    } else {
      theater.style.display = 'none';
    }
  });
}