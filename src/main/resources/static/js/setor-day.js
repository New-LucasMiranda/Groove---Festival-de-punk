document.addEventListener('DOMContentLoaded', function() {
    const setorSelect = document.getElementById('setor');
    const confirmButton = document.getElementById('finalizar-btn');
    const setorEscolhido = document.getElementById('setorEscolhido');
    const successModalElement = new bootstrap.Modal(document.getElementById('popup-confirm')); // Inicialize o modal
    const closeButton = document.getElementById('btn-fechar'); // Botão para fechar o modal

      // Função para obter o parâmetro 'token' da URL
      function getParameterByName(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name); // Retorna o valor do parâmetro 'token'
    }
    
    const token = getParameterByName('token');
    console.log(token);
    
    setorSelect.addEventListener('change', function() {
        const setor = setorSelect.value;

        if (setor) {
            confirmButton.disabled = false;
        } else {
            confirmButton.disabled = true;
        }
    });

    const setorForm = document.getElementById('setorForm');
    setorForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Previne o comportamento padrão de envio do formulário

        const setor = setorSelect.value;
        setorEscolhido.textContent = setor;
        successModalElement.show(); // Mostrar o modal de confirmação
    });

    closeButton.addEventListener('click', function() {
        successModalElement.hide(); // Fechar o modal de confirmação
    });

    confirmButton.addEventListener('click', async () => {

        try {
            // Obtém o CPF associado ao token
            const cpf = await getUserIdFromToken(token);
    
            if (!cpf) {
                console.error("CPF não encontrado para o token fornecido.");
                failureModalElement.show();
                return; // Interrompe o processo
            }
    
            // Realiza as atualizações necessárias
            await updatePrimReserva(cpf);
            await updateSituacao(cpf);
            await InvalidateToken(token);   
    
            // Exibe o modal de sucesso após todas as operações concluídas
            successModalElement.show();
            
        } catch (error) {
            console.error("Erro ao processar reservas ou invalidar o token:", error);
          
        }

    });

    async function getUserIdFromToken(token) {
        try {
            // URL do controlador Spring Boot
            const url = "http://localhost:8080/tokens/get-user-id?token="+token;

            // Fazendo a requisição GET para o controlador
            const response = await fetch(url, {
                method: 'GET', 
                headers: {
                }
            });

            // Verifica se a resposta foi bem-sucedida
            if (response.ok) {
                const cpf = await response.text(); // Recebe o CPF da resposta
                console.log("CPF obtido: ", cpf);
                return cpf; // Retorna o CPF obtido
            } else {

                console.error("Erro ao obter CPF ");
               // Lança erro se a resposta não for ok
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            alert("Houve um erro ao tentar obter o CPF com o token.");
        }
    } 

    async function InvalidateToken(token) {
        try {
            // URL do controlador Spring Boot
            const url = "http://localhost:8080/tokens/invalidate?token="+token;

            // Fazendo a requisição GET para o controlador
            const response = await fetch(url, {
                method: "POST", 
                headers: {
                }
            });

            if (response.ok) {
                console.log("Token invalidado com sucesso");
            } else if (response.status === 400) {
                
                console.log("Erro ao invalidar token:",response.status);
            } else {
                console.log("Erro ao invalidar token. Status:", response.status);
            }
        } catch (error) {
            console.log("Erro na requisição:", error);
        }
    }
    async function updateSituacao(cpf) {
        try {
            // Monta a URL com o CPF
            let url = "http://localhost:8080/users/"+cpf+"/situacao";
    
            // Faz a requisição PUT para o endpoint
            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json", // Define o tipo do conteúdo
                },
                body: JSON.stringify(false), // Envia o valor de "situacao" no corpo
            });
    
            // Trata a resposta do backend
            if (response.status === 200) {
                console.log("Situação atualizada com sucesso.");
            } else if (response.status === 404) {
                console.log("Usuário não encontrado.");
            } else {
                console.log("Erro ao atualizar a situação. Status:", response.status);
            }
        } catch (error) {
            console.error("Erro na comunicação com o servidor:", error);
        }
    }

       async function updatePrimReserva(cpf) {
            const setor = setorSelect.value; // Assumindo que setorSelect é um elemento do tipo select onde o usuário escolhe o setor
        
            try {
                // Primeira requisição para obter os dados do usuário
                let url = "http://localhost:8080/users/" + cpf;
                const response = await fetch(url, {
                    headers: { "Content-Type": "application/json" },
                });
        
                if (response.status === 200) {
                    const data = await response.json();
                    console.log(data.dia);
        
                    // URL para o PUT (sem basear em data.dia, apenas usando o cpf e setor)
                    let positionUrl = "http://localhost:8080/users/" + cpf + "/prim-reserva";
        
                    // Faz a requisição para atualizar a posição no método PUT
                    const resp = await fetch(positionUrl, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(setor),
                    });
        
                    if (resp.status === 200) {
                        
                            // Decrementa com base em Prim ou Seg prefixado ao setor
                            let day = "";
                            if (data.dia === "1") {
                                day = "Prim" + setor;
                            } else if (data.dia === "2") {
                                day = "Seg" + setor;
                            }
        
                            let sectorUrl = "http://localhost:8080/sectors/" +day+ "/decrement";
                            const decrementResp = await fetch(sectorUrl, {
                                method: "PATCH",
                                headers: { "Content-Type": "application/json" },
                            });
        
                            if (decrementResp.status === 200) {
                                console.log("Setor atualizado com sucesso.");
                            } else {
                                console.log("Erro ao atualizar o setor. Status: " + decrementResp.status, "warning");
                            }
                        
                            const dequeueUrl = "http://localhost:8080/queues/"+ data.dia+ "/dequeue";
                            const dequeueResponse = await fetch(dequeueUrl, {
                                method: "DELETE",
                                headers: { "Content-Type": "application/json" },
                            });
        
                            if (dequeueResponse.status === 200) {
                                console.log("Dequeue realizado com sucesso.");
                            } else {
                                console.log("Erro ao realizar dequeue. Status: " + dequeueResponse.status);
                            }
                        
                    } else {
                        console.log("Erro ao atualizar a posição do usuário.", "warning");
                    }
                } else {
                    console.log("CPF não encontrado. Por favor, verifique seu CPF.", "warning");
                }
            } catch (error) {
                console.error("Erro ao buscar dados do usuário:", error);
                throw error; // Repassa o erro para tratamento externo
            }
        }
      

});
