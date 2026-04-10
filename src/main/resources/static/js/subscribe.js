// Configuração da API
API.base = window.location.origin;

document.addEventListener('DOMContentLoaded', function () {

    const cards = document.querySelectorAll('.day-elipse');
    const tableContainer = document.getElementById('ticket-table');
    const tableBody = document.querySelector('table tbody');
    const continueBtn = document.getElementById('btn-finalizar');
    const finalizeSubscribeBtn = document.getElementById('finalizar-btn');
    const formContainer = document.getElementById('checkout-form-container');
    const removeButton = document.getElementById('btn-remover');
    const daySelectContainer = document.getElementById('day-select-container');

    const confirmationModal = new bootstrap.Modal(document.getElementById('confirmationModal'));
    const submissionConfirmationModal = new bootstrap.Modal(document.getElementById('successModal'));
    const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
    const modal = new bootstrap.Modal(document.getElementById('modalAlert'), {
        backdrop: 'static',
        keyboard: false
    });

    const confirmRemoveButton = document.getElementById('confirm-remove-button');

    let ticketSelected = false;
    let selectedTicketType = null;

    // =========================
    // 🔹 SELEÇÃO DE INGRESSO
    // =========================

    cards.forEach(card => {
        card.addEventListener('click', function () {
            const ticketType = this.getAttribute('data-ticket');

            if (ticketSelected) {
                modal.show();
            } else {
                updateTicketTable(ticketType);
                ticketSelected = true;
                selectedTicketType = ticketType;

                tableContainer.classList.remove('d-none');
                continueBtn.classList.remove('d-none');

                daySelectContainer.style.display = (ticketType === 'Groove Day') ? 'block' : 'none';
            }
        });
    });

    function updateTicketTable(ticketType) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td></td>
            <td>Groove</td>
            <td>${ticketType}</td>
            <td></td>
            <td>
                <button class="btn btn-danger" onclick="removeTicket(this)">Remover</button>
            </td>
        `;
        tableBody.appendChild(row);
    }

    window.removeTicket = function (button) {
        if (formContainer.style.display === 'block') {
            confirmationModal.show();
            confirmRemoveButton.onclick = () => location.reload();
        } else {
            button.closest('tr').remove();
            ticketSelected = false;
            selectedTicketType = null;

            tableContainer.classList.add('d-none');
            continueBtn.classList.add('d-none');
            daySelectContainer.style.display = 'none';
        }
    };

    // =========================
    // 🔹 UI CONTROLES
    // =========================

    removeButton?.addEventListener('click', () => {
        if (formContainer.style.display === 'block') {
            confirmationModal.show();
            confirmRemoveButton.onclick = () => location.reload();
        } else {
            formContainer.remove();
            tableContainer.classList.add('d-none');
            daySelectContainer.style.display = 'none';
        }
    });

    continueBtn?.addEventListener('click', () => {
        formContainer.style.display = 'block';
        continueBtn.style.display = 'none';
    });

    // =========================
    // 🔹 VALIDAÇÃO CPF
    // =========================

    function validateCPF(input) {
        const cpf = input.value.replace(/\D/g, '');

        if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) {
            input.classList.add('is-invalid');
            return false;
        }

        const calc = (arr, len) => {
            let sum = 0;
            for (let i = 0; i < len; i++) {
                sum += parseInt(arr[i]) * (len + 1 - i);
            }
            const rest = sum % 11;
            return rest < 2 ? 0 : 11 - rest;
        };

        const v1 = calc(cpf.slice(0, 9).split(''), 9);
        const v2 = calc(cpf.slice(0, 10).split(''), 10);

        if (v1 !== +cpf[9] || v2 !== +cpf[10]) {
            input.classList.add('is-invalid');
            return false;
        }

        input.classList.remove('is-invalid');
        return true;
    }

    // =========================
    // 🔹 SUBMIT
    // =========================

    finalizeSubscribeBtn?.addEventListener('click', async (event) => {
        event.preventDefault();

        const cpfInput = document.getElementById('document-number');

        if (!validateCPF(cpfInput)) return;

        const email = document.getElementById('email').value;
        const name = document.getElementById('name').value + " " + document.getElementById('last-name').value;
        const age = document.getElementById('age').value;

        let dia, situacao = true, primReserva = null, segReserva = null;

        switch (selectedTicketType) {
            case "Groove Day":
                dia = document.getElementById('day-select').value;
                break;
            case "Groove Pass":
                dia = "Pass";
                break;
            case "Groove Vip":
                dia = "VIP";
                situacao = false;
                primReserva = "VIP";
                segReserva = "VIP";
                break;
        }

        const data = {
            cpf: cpfInput.value,
            email,
            nome: name,
            idade: age,
            dia,
            primReserva,
            segReserva,
            "situação": situacao
        };

        try {
            // 🔹 Criar usuário
            const endpoint = (dia === "VIP") ? "/users/VIP" : "/users";
            await API.post(endpoint, data);

            // 🔹 Atualizar setor VIP
            if (dia === "VIP") {
                await API.patch(`/sectors/VIP/decrement`);
                console.log("Setor VIP atualizado");
            }

            submissionConfirmationModal.show();

        } catch (error) {
            console.error("Erro:", error);
            errorModal.show();
        }
    });

});