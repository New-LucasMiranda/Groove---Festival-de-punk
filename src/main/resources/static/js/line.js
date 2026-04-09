// Configuração da API
API.base = window.location.origin;

// Elementos DOM
const cpfInput = document.getElementById('cpf');
const positionOutput = document.getElementById('position');
const nameOutput = document.getElementById('name');
const tiketOutput = document.getElementById('ticket');
const btnVerify = document.getElementById('verify');
const btnExit = document.getElementById('exit-btn');
const popConfirmExit = document.getElementById('successModal');
const confirmationModalElement = document.getElementById('confirmationModal');
const confirmationCheckbox = document.getElementById('conform-checkbox');
const confirmExitButton = document.getElementById('confirm-exit-btn');

// Modais Bootstrap
const confirmationModal = new bootstrap.Modal(confirmationModalElement);
const popupExit = new bootstrap.Modal(popConfirmExit);

// Máscara CPF
function maskCPF(value) {
    return value.replace(/\D/g, "")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

cpfInput.addEventListener('input', function() {
    cpfInput.value = maskCPF(cpfInput.value);
});

// Botão verificar
btnVerify.addEventListener("click", async () => {
    const cpf = cpfInput.value.trim();

    if (!isValidCPF(cpf)) {
        showAlert("CPF inválido. Por favor, tente novamente.", "danger");
        clearFields();
        return;
    }

    await getUserData(cpf);
});

// Botão sair da fila
btnExit.addEventListener("click", async () => {
    const cpf = cpfInput.value.trim();

    if (!isValidCPF(cpf)) {
        showAlert("CPF inválido. Por favor, tente novamente.", "danger");
        clearFields();
        return;
    }

    confirmationModal.show();

    confirmExitButton.addEventListener(
        'click',
        async function handleExitClick() {
            if (confirmationCheckbox.checked) {
                try {
                    await deleteUserDataOnQueue(cpf);
                    await updateUserSituation(cpf);

                    popupExit.show();
                    confirmationModal.hide();

                    confirmExitButton.removeEventListener('click', handleExitClick);
                } catch (error) {
                    console.error("Erro ao sair da fila:", error);
                }
            } else {
                showAlert("Por favor, confirme que deseja sair da fila.", "warning");
            }
        }
    );
});

// Validação CPF
function isValidCPF(cpf) {
    const cpfRegex = /^\d{3}\.\d{3}\.\d{3}-\d{2}$/;
    if (!cpfRegex.test(cpf)) return false;

    const cpfNumbers = cpf.replace(/\D/g, '');
    if (cpfNumbers.length !== 11) return false;

    const calculateVerifier = (cpfArray, length) => {
        let sum = 0;
        for (let i = 0; i < length; i++) {
            sum += parseInt(cpfArray[i]) * (length + 1 - i);
        }
        const remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    };

    const firstNineDigits = cpfNumbers.slice(0, 9).split('');
    const firstVerifier = calculateVerifier(firstNineDigits, 9);

    const firstTenDigits = cpfNumbers.slice(0, 10).split('');
    const secondVerifier = calculateVerifier(firstTenDigits, 10);

    return (
        firstVerifier === parseInt(cpfNumbers[9]) &&
        secondVerifier === parseInt(cpfNumbers[10])
    );
}

// 🔹 Buscar dados do usuário
async function getUserData(cpf) {
    try {
        const data = await API.get(`/users/${cpf}`);

        nameOutput.value = data.nome;

        let ticketText = '';
        switch (data.dia) {
            case "1": ticketText = 'Day One'; break;
            case "2": ticketText = 'Day Two'; break;
            case "VIP": ticketText = 'VIP'; break;
            default: ticketText = 'Pass';
        }

        tiketOutput.value = ticketText;

        if (data.dia === "VIP") {
            showAlert("Seu ingresso é VIP. Você não precisa entrar na fila.", "info");
            positionOutput.value = '';
            return;
        }

        const position = await API.get(`/queues/${data.dia}/position/${cpf}`);

        if (data.situação === false) {
            showAlert("Você não está na fila no momento.", "warning");
        } else {
            positionOutput.value = position + "°";
        }

    } catch (error) {
        console.error("Erro ao buscar dados:", error);
        showAlert("CPF não encontrado.", "warning");
        clearFields();
    }
}

// 🔹 Remover da fila
async function deleteUserDataOnQueue(cpf) {
    try {
        const data = await API.get(`/users/${cpf}`);

        await API.delete(`/queues/${data.dia}/remove/${cpf}`);

        console.log("Desistência concluída com sucesso.");
        clearFields();

    } catch (error) {
        console.error("Erro:", error);
        showAlert("Erro ao processar saída da fila.", "danger");
    }
}

// 🔹 Atualizar situação
async function updateUserSituation(cpf) {
    try {
        await API.put(`/users/${cpf}/situacao`, false);
        console.log("Situação atualizada com sucesso");

    } catch (error) {
        console.error("Erro:", error);
        showAlert("Erro ao atualizar situação.", "danger");
    }
}

// Alertas
function showAlert(message, type) {
    const alertContainer = document.getElementById('result');
    alertContainer.innerHTML = '';
    alertContainer.innerHTML = `<div class="alert alert-${type} mt-3">${message}</div>`;
}

// Limpar campos
function clearFields() {
    positionOutput.value = '';
    nameOutput.value = '';
    tiketOutput.value = '';
}

// JQuery validação
$(document).ready(function () {
    console.log("JQuery está funcionando!");

    $('#cpfForm').on('submit', function (e) {
        e.preventDefault();

        const cpf = $('#cpf').val().trim();

        if (!isValidCPF(cpf)) {
            showAlert("CPF inválido. Por favor, tente novamente.", "danger");
            clearFields();
            return;
        }
    });
});