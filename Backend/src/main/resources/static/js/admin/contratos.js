document.addEventListener('DOMContentLoaded', () => {
    const filterButtons = document.querySelectorAll('.pill-filters .filter-btn');
    const cards = document.querySelectorAll('.contract-card');

    filterButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            // 1. Alternar clase active en los botones
            filterButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const filter = btn.getAttribute('data-filter');

            // 2. Filtrar las tarjetas de contrato
            cards.forEach(card => {
                const estado = card.getAttribute('data-estado');

                if (filter === 'Todos') {
                    card.style.display = '';
                } else if (filter === 'ACEPTADA') {
                    // "En Curso" agrupa tanto ACEPTADA como PENDIENTE
                    if (estado === 'ACEPTADA' || estado === 'PENDIENTE') {
                        card.style.display = '';
                    } else {
                        card.style.display = 'none';
                    }
                } else if (filter === 'FINALIZADA') {
                    if (estado === 'FINALIZADA') {
                        card.style.display = '';
                    } else {
                        card.style.display = 'none';
                    }
                }
            });
        });
    });
});