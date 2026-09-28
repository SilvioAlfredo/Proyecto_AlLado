document.addEventListener('DOMContentLoaded', () => {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const postCards = document.querySelectorAll('.post-card');

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            // 1. Alternar clase activa en tabs
            tabButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            // 2. Mostrar u ocultar posts
            const tabVal = btn.getAttribute('data-tab');
            postCards.forEach(card => {
                const tag = card.getAttribute('data-tag');
                if (tabVal === 'Recientes') {
                    card.style.display = 'block';
                } else if (tabVal === 'Populares') {
                    card.style.display = tag === 'Popular' ? 'block' : 'none';
                }
            });
        });
    });
});