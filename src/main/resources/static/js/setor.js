// Configuração da API
API.base = window.location.origin;

document.addEventListener('DOMContentLoaded', function() {

    const setorSelect2 = document.getElementById('setor2');
    const setorSelect1 = document.getElementById("setor");
    const confirmButton = document.getElementById('finalizar-btn');
    const setorEscolhido = document.getElementById('setorEscolhido');

    const successModalElement = new bootstrap.Modal(document.getElementById('popup-confirm'));
    // const failureModalElement = new bootstrap.Modal(document.getElementById('popup-failure'));

    const closeButton = document.getElementById('btn-fechar');
    const closeButtonFailure = document.getElementById('btn-fechar-falha');

    // 🔹 Token
    function getParameterByName(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    }

    const token = getParameterByName('token');
    console.log(token);

    // 🔹 Habilita botão
    setorSelect2.addEventListener('change', function() {
        confirmButton.disabled = !setorSelect2.value;
    });

    // 🔹 Form
    const setorForm = document.getElementById('setorForm');
    setorForm.addEventListener('submit', function(event) {
        event.preventDefault();
        setorEscolhido.textContent = setorSelect1.value + " e " + setorSelect2.value;
    });

    // 🔹 Modais
    closeButton.addEventListener('click', () => successModalElement.hide());

    if (closeButtonFailure) {
        closeButtonFailure.addEventListener('click', () => {
            if (typeof failureModalElement !== "undefined") {
                failureModalElement.hide();
            }
        });
    }

    // 🔹 Confirmar
    confirmButton.addEventListener('click', async () => {
        try {
            const cpf = await getUserIdFromToken(token);

            if (!cpf) {
                console.error("CPF não encontrado.");
                return;
            }

            await updatePrimReserva(cpf);
            await updateSegReserva(cpf);
            await updateSituacao(cpf);
            await invalidateToken(token);

            successModalElement.show();

        } catch (error) {
            console.error("Erro geral:", error);
        }
    });

    // =========================
    // 🔹 API FUNCTIONS
    // =========================

    async function getUserIdFromToken(token) {
        try {
            const cpf = await API.get(`/tokens/get-user-id?token=${token}`);
            console.log("CPF:", cpf);
            return cpf;

        } catch (error) {
            console.error("Erro ao obter CPF:", error);
        }
    }

    async function invalidateToken(token) {
        try {
            await API.post(`/tokens/invalidate?token=${token}`);
            console.log("Token invalidado");

        } catch (error) {
            console.error("Erro ao invalidar token:", error);
        }
    }

    async function updateSituacao(cpf) {
        try {
            await API.put(`/users/${cpf}/situacao`, false);
            console.log("Situação atualizada");

        } catch (error) {
            console.error("Erro situação:", error);
        }
    }

    // 🔹 PRIMEIRA RESERVA
    async function updatePrimReserva(cpf) {
        const setor = setorSelect1.value;

        try {
            const data = await API.get(`/users/${cpf}`);

            await API.put(`/users/${cpf}/prim-reserva`, setor);

            const setoresValidos = ["Pista", "PistaPremium", "Camarote", "VIP"];
            const diasEspeciais = ["VIP", "Pass"];

            if (diasEspeciais.includes(data.dia) && setoresValidos.includes(setor)) {

                await API.patch(`/sectors/Prim${setor}/decrement`);
                await API.patch(`/sectors/Seg${setor}/decrement`);

                console.log("Setores Prim e Seg decrementados");

            } else {
                let day = "";

                if (data.dia === "1") day = "Prim" + setor;
                else if (data.dia === "2") day = "Seg" + setor;

                await API.patch(`/sectors/${day}/decrement`);
                console.log("Setor decrementado:", day);
            }

            if (data.dia !== "VIP") {
                await API.delete(`/queues/${data.dia}/dequeue`);
                console.log("Dequeue realizado");
            } else {
                console.log("Dequeue ignorado (VIP)");
            }

        } catch (error) {
            console.error("Erro Prim:", error);
            throw error;
        }
    }

    // 🔹 SEGUNDA RESERVA
    async function updateSegReserva(cpf) {
        const setor = setorSelect2.value;

        try {
            const data = await API.get(`/users/${cpf}`);

            await API.put(`/users/${cpf}/seg-reserva`, setor);

            const setoresValidos = ["Pista", "PistaPremium", "Camarote", "VIP"];
            const diasEspeciais = ["VIP", "Pass"];

            if (diasEspeciais.includes(data.dia) && setoresValidos.includes(setor)) {

                await API.patch(`/sectors/Prim${setor}/decrement`);
                await API.patch(`/sectors/Seg${setor}/decrement`);

                console.log("Setores Prim e Seg decrementados");

            } else {
                let day = "";

                if (data.dia === "1") day = "Prim" + setor;
                else if (data.dia === "2") day = "Seg" + setor;

                await API.patch(`/sectors/${day}/decrement`);
                console.log("Setor decrementado:", day);
            }

            if (data.dia !== "VIP") {
                await API.delete(`/queues/${data.dia}/dequeue`);
                console.log("Dequeue realizado");
            } else {
                console.log("Dequeue ignorado (VIP)");
            }

        } catch (error) {
            console.error("Erro Seg:", error);
            throw error;
        }
    }

});