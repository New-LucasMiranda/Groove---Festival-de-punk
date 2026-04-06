// Configuração da API
API.base = window.location.origin;

document.addEventListener('DOMContentLoaded', function() {

    const setorSelect = document.getElementById('setor');
    const confirmButton = document.getElementById('finalizar-btn');
    const setorEscolhido = document.getElementById('setorEscolhido');
    const successModalElement = new bootstrap.Modal(document.getElementById('popup-confirm'));
    const closeButton = document.getElementById('btn-fechar');

    // 🔹 Pegar token da URL
    function getParameterByName(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    }

    const token = getParameterByName('token');
    console.log(token);

    // 🔹 Habilitar botão
    setorSelect.addEventListener('change', function() {
        confirmButton.disabled = !setorSelect.value;
    });

    // 🔹 Submit formulário
    const setorForm = document.getElementById('setorForm');
    setorForm.addEventListener('submit', function(event) {
        event.preventDefault();
        setorEscolhido.textContent = setorSelect.value;
        successModalElement.show();
    });

    closeButton.addEventListener('click', function() {
        successModalElement.hide();
    });

    // 🔹 Confirmar ação
    confirmButton.addEventListener('click', async () => {
        try {
            const cpf = await getUserIdFromToken(token);

            if (!cpf) {
                console.error("CPF não encontrado para o token.");
                return;
            }

            await updatePrimReserva(cpf);
            await updateSituacao(cpf);
            await invalidateToken(token);

            successModalElement.show();

        } catch (error) {
            console.error("Erro geral:", error);
        }
    });

    // ================================
    // 🔹 API FUNCTIONS
    // ================================

        async function getUserIdFromToken(token) {
            try {
                const data = await API.get(`/tokens/get-user-id?token=${token}`);

                if (!data?.cpf) {
                    throw new Error("Resposta inválida do servidor");
                }

                console.log("CPF obtido:", data.cpf);
                return data.cpf;

            } catch (error) {
                console.error("Erro ao obter CPF:", error);
                alert("Erro ao validar token.");
            }
        }

    async function invalidateToken(token) {
        try {
            await API.post(`/tokens/invalidate?token=${token}`);
            console.log("Token invalidado com sucesso");

        } catch (error) {
            console.error("Erro ao invalidar token:", error);
        }
    }

    async function updateSituacao(cpf) {
        try {
            await API.put(`/users/${cpf}/situacao`, false);
            console.log("Situação atualizada com sucesso");

        } catch (error) {
            console.error("Erro ao atualizar situação:", error);
        }
    }

    async function updatePrimReserva(cpf) {
        const setor = setorSelect.value;

        try {
            const data = await API.get(`/users/${cpf}`);
            console.log(data.dia);

            // Atualiza reserva
            await API.put(`/users/${cpf}/prim-reserva`, setor);

            // Define dia/setor
            let day = "";
            if (data.dia === "1") {
                day = "Prim" + setor;
            } else if (data.dia === "2") {
                day = "Seg" + setor;
            }

            // Decrementa setor
            await API.patch(`/sectors/${day}/decrement`);
            console.log("Setor atualizado com sucesso.");

            // Remove da fila
            await API.delete(`/queues/${data.dia}/dequeue`);
            console.log("Dequeue realizado com sucesso.");

        } catch (error) {
            console.error("Erro ao atualizar reserva:", error);
            throw error;
        }
    }

});