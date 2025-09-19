document.addEventListener('DOMContentLoaded', function() {
    const checkbox1 = document.getElementById('flexCheckDefault');
    const checkbox2 = document.getElementById('flexCheckChecked');
    const finalizarBtn = document.getElementById('finalizar-btn');

    function updateButtonState() {
        finalizarBtn.disabled = !(checkbox1.checked && checkbox2.checked);
    }

    checkbox1.addEventListener('change', updateButtonState);
    checkbox2.addEventListener('change', updateButtonState);
});
