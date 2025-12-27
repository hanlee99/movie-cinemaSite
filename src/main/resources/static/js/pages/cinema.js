export function initCinema() {
  const chipGroups = {
    region: document.querySelectorAll('.chip[data-region]'),
    brand: document.querySelectorAll('.chip[data-brand]'),
    special: document.querySelectorAll('.chip[data-special]')
  };

  const theaterContainer = document.getElementById('theaterResults');
  const emptyState = createEmptyState(theaterContainer);

  const filters = {
    brand: 'all',
    region: 'all',
    special: 'all'
  };

  Object.entries(chipGroups).forEach(([type, chips]) => {
    chips.forEach(chip => {
      chip.addEventListener('click', () => {
        const value = chip.dataset[type];

        // 동일 항목 두 번 클릭 시 '전체'로 초기화
        if (filters[type] === value && chip.classList.contains('active')) {
          filters[type] = 'all';
          deactivateGroup(chips);
          const defaultChip = Array.from(chips).find(c => c.dataset[type] === 'all');
          if (defaultChip) {
            defaultChip.classList.add('active');
          }
        } else {
          deactivateGroup(chips);
          chip.classList.add('active');
          filters[type] = value;
        }

        filterTheaters(filters, theaterContainer, emptyState);
      });
    });
  });

  // 초기 필터 적용
  filterTheaters(filters, theaterContainer, emptyState);
}

function deactivateGroup(chips) {
  chips.forEach(c => c.classList.remove('active'));
}

function filterTheaters(filters, container, emptyState) {
  const theaters = container
    ? container.querySelectorAll('.theater-card')
    : document.querySelectorAll('.theater-card');

  let visibleCount = 0;

  theaters.forEach(theater => {
    const brand = (theater.dataset.brand || '').toLowerCase();
    const region = theater.dataset.region || '';
    const special = (theater.dataset.special || theater.dataset.specialties || '').toLowerCase();

    const showBrand = filters.brand === 'all' || brand === filters.brand.toLowerCase();
    const showRegion = filters.region === 'all' || region === filters.region;
    const showSpecial =
      filters.special === 'all' || special.includes(filters.special.toLowerCase());

    if (showBrand && showRegion && showSpecial) {
      theater.style.display = 'block';
      visibleCount += 1;
    } else {
      theater.style.display = 'none';
    }
  });

  if (!container) {
    return;
  }

  if (visibleCount === 0) {
    emptyState.style.display = 'flex';
    container.appendChild(emptyState);
  } else {
    emptyState.style.display = 'none';
  }
}

function createEmptyState(container) {
  const existing = container?.querySelector('.theater-empty-state');
  if (existing) {
    return existing;
  }

  const emptyState = document.createElement('div');
  emptyState.className =
    'theater-empty-state flex flex-col items-center justify-center py-10 text-gray-500';
  emptyState.innerHTML = `
    <p class="font-medium">조건에 맞는 영화관이 없습니다.</p>
    <p class="text-sm mt-2">필터를 리셋하거나 다른 조건을 선택해보세요.</p>
  `;
  emptyState.style.display = 'none';

  return emptyState;
}

