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
// Criação de uma instância do modal Bootstrap
const confirmationModal = new bootstrap.Modal(confirmationModalElement);
const popupExit = new bootstrap.Modal(popConfirmExit); // Inicializa o de saida concluida

function maskCPF(value) {
    return value.replace(/\D/g, "")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

// Event listener para aplicar a máscara de CPF e impedir inserção de letras
cpfInput.addEventListener('input', function() {
    cpfInput.value = maskCPF(cpfInput.value);
});

// Adiciona evento ao botão
btnVerify.addEventListener("click", async () => {
    const cpf = cpfInput.value.trim();

    if (!isValidCPF(cpf)) {
        showAlert("CPF inválido. Por favor, tente novamente.", "danger");
        clearFields();
        return;
    }

    await getUserData(cpf);

});

btnExit.addEventListener("click", async () => {
    const cpf = cpfInput.value.trim();

    try {
        // Verifica se o CPF é válido
        if (!isValidCPF(cpf)) {
            showAlert("CPF inválido. Por favor, tente novamente.", "danger");
            clearFields();
            return;
        }

        // Abre o modal de confirmação
        confirmationModal.show();

        // Adiciona um evento ao botão do modal para confirmar a saída da fila
        confirmExitButton.addEventListener(
            'click',
            async function handleExitClick() {
                if (confirmationCheckbox.checked) {
                    try {
                        // Tenta atualizar as reservas e situação do usuário
                        await deleteUserDataOnQueue(cpf);
                        await updateUserSituation(cpf);

                        // Exibe o modal de sucesso após ambas as atualizações
                        popupExit.show();

                        // Fecha o modal de confirmação
                        confirmationModal.hide();

                        // Remove o evento do botão após a execução
                        confirmExitButton.removeEventListener('click', handleExitClick);
                    } catch (error) {
                        console.error("Erro ao sair da fila:", error);
                    }
                } else {
                    showAlert("Por favor, confirme que deseja sair da fila.", "warning");
                }
            }
        );
    } catch (error) {
        console.error("Erro ao processar saída da fila:", error);
    }
});



// Valida o CPF
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

// Obtém os dados do usuário pelo CPF
async function getUserData(cpf) {
    try {
        // Primeira requisição para obter os dados do usuário
        let url = "http://localhost:8080/users/" + cpf;
        const response = await fetch(url, {
            headers: { "Content-Type": "application/json" },
        });

        if (response.status === 200) {
            const data = await response.json();
            nameOutput.value = data.nome;

            // Definindo o texto do ingresso (ticket)
            let ticketText = '';
            switch (data.dia) {
                case "1":
                    ticketText = 'Day One';
                    break;
                case "2":
                    ticketText = 'Day Two';
                    break;
                case "VIP":
                    ticketText = 'VIP';
                    break;
                default:
                    ticketText = 'Pass';
                    break;
            }

            tiketOutput.value = ticketText;

            // Verifica se o ingresso é VIP
            if (data.dia === "VIP") {
                showAlert("Seu ingresso é VIP. Você não precisa entrar na fila.", "info");
                positionOutput.value = '';  // Não exibe a posição, pois não é necessário
                return;  // Retorna sem fazer a chamada pela posição
            }

            // Caso contrário, continua com a chamada para obter a posição na fila
            let positionUrl = "http://localhost:8080/queues/" + data.dia + "/position/" + cpf;
            const resp = await fetch(positionUrl, {
                headers: { "Content-Type": "application/json" },
            });

            if (resp.status === 200) {
                const dado = await resp.json();

                // Verifica se o dado retornado é -1 (não está na fila)
                if (data.situação === false) {
                    showAlert("Você não está na fila no momento.", "warning");
                } else {
                    positionOutput.value = dado + "°";
                }
            } else {
                showAlert("CPF não encontrado. Por favor, verifique seu CPF.", "warning");
                clearFields();
            }
        } else {
            showAlert("CPF não encontrado. Por favor, verifique seu CPF.", "warning");
            clearFields();
        }
    } catch (error) {
        console.error("Erro ao buscar dados do usuário:", error);
        showAlert("Erro ao buscar dados do servidor.", "danger");
    }
}


async function deleteUserDataOnQueue(cpf) {

    try {
        // Primeira requisição para obter os dados do usuário
        let url = "http://localhost:8080/users/" + cpf;
        const response = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        });

        if (response.status === 200) {
            const data = await response.json();

            let positionUrl = "http://localhost:8080/queues/" + data.dia + "/remove/" + cpf; 
            const resp = await fetch(positionUrl, {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
            });

            if (resp.status === 200) {
                console.log("Desistencia conxluida com sucesso.");   
                clearFields();  
            }

        } else {
            showAlert("CPF não encontrado. Por favor, verifique seu CPF.", "warning");           
        }
    } catch (error) {
        console.error("Erro ao buscar dados do usuário:", error);
        showAlert("Erro ao buscar dados do servidor.", "danger");
    }

}
async function updateUserSituation(cpf) {

    try {
        // Primeira requisição para obter os dados do usuário
        let url = "http://localhost:8080/users/" + cpf+ "/situacao";

        const response = await fetch(url, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(false),
        });

        if (response.status == 200) {           

            console.log("Situação atualizada com sucesso")     
            
        } else {
            console.log("Erro ao atualizar a situação", response.status);
        }
    } catch (error) {
        console.error("Erro ao buscar dados do usuário:", error);
        showAlert("Erro ao buscar dados do servidor.", "danger");
    }

}


// Mostra alertas na página
// Mostra alertas na página
function showAlert(message, type) {
    const alertContainer = document.getElementById('result');
    
    // Limpa o conteúdo do container de alertas antes de adicionar a nova mensagem
    alertContainer.innerHTML = ''; 

    // Adiciona o novo alerta
    alertContainer.innerHTML = `<div class="alert alert-${type} mt-3">${message}</div>`;
}


// Limpa os campos de saída
function clearFields() {
    positionOutput.value = '';
    nameOutput.value = '';
    tiketOutput.value = '';
}

// Validação do formulário com JQuery
$(document).ready(function () {
    console.log("JQuery está funcionando!");

    $('#cpfForm').on('submit', function (e) {
        e.preventDefault();
        console.log("Formulário enviado!");

        const cpf = $('#cpf').val().trim();

        if (!isValidCPF(cpf)) {
            showAlert("CPF inválido. Por favor, tente novamente.", "danger");
            clearFields();
            return;
        }
    });
});
