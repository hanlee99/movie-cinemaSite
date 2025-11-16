// app.js
import { initBoxoffice } from './sections/boxoffice.js';
import { initCinema } from './sections/cinema.js';
// import { initMap } from './sections/showtime.js';

document.addEventListener('DOMContentLoaded', () => {
  const boxofficeSection = document.querySelector('#boxoffice');
  const cinemaSection = document.querySelector('#cinema');
  const mapSection = document.querySelector('#map');

  const sections = [boxofficeSection, cinemaSection, mapSection];
  const navLinks = document.querySelectorAll('header nav a');

  initBoxoffice();
  initCinema();
  // initMap();

  sections.forEach(sec => (sec.style.display = 'none'));
  boxofficeSection.style.display = 'block';

  navLinks.forEach(link => {
    link.addEventListener('click', e => {
      e.preventDefault();

      const targetId = link.getAttribute('href').replace('#', '');
      const targetSection = document.getElementById(targetId);

      window.scrollTo({ top: 0, behavior: 'smooth' });

      // 모든 섹션 숨기기
      sections.forEach(sec => (sec.style.display = 'none'));

      // 선택한 섹션만 표시
      targetSection.style.display = 'block';
    });
  });
});
