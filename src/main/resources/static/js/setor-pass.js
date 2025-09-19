document.addEventListener('DOMContentLoaded', function() {
    const setorSelect2 = document.getElementById('setor2');
    const setorSelect1 = document.getElementById("setor1");
    const confirmButton = document.getElementById('finalizar-btn');
    const setorEscolhido = document.getElementById('setorEscolhido');
    const successModalElement = new bootstrap.Modal(document.getElementById('popup-confirm')); // Inicialize o modal
    const failureModalElement = new bootstrap.Modal(document.getElementById('popup-failure'));
    const closeButton = document.getElementById('btn-fechar'); // Botão para fechar o modal
    const closeButtonFailure = document.getElementById('btn-fechar-falha');

    console.log(successModalElement,failureModalElement);


     // Função para obter o parâmetro 'token' da URL
     function getParameterByName(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name); // Retorna o valor do parâmetro 'token'
    }
    
    const token = getParameterByName('token');
    console.log(token);

       // Habilita ou desabilita o botão de confirmação com base na escolha de setor
       setorSelect2.addEventListener('change', function() {
        const setor = setorSelect2.value;
        console.log(setor);

        if (setor) {
            confirmButton.disabled = false;
        } else {
            confirmButton.disabled = true;
        }
    });

    const setorForm = document.getElementById('setorForm');
    setorForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Previne o comportamento padrão de envio do formulário

        const setor1 = setorSelect1.value;
        const setor2 = setorSelect2.value;

        setorEscolhido.textContent = setor1 + " e " + setor2;
    });

    closeButton.addEventListener('click', function() {
        successModalElement.hide(); // Fechar o modal de confirmação
    });

    closeButtonFailure.addEventListener('click', function() {
        failureModalElement.hide(); // Fechar o modal de confirmação
    });


    confirmButton.addEventListener('click', async () => {
 // Variável de controle para exibir o modal de erro apenas uma vez

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
            await updateSegReserva(cpf);
            await updateSituacao(cpf);
            await InvalidateToken(token);   
    
            // Exibe o modal de sucesso após todas as operações concluídas
            successModalElement.show();
            
        } catch (error) {
            console.error("Erro ao processar reservas ou invalidar o token:", error);
          
        }
    
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

        async function updatePrimReserva(cpf) {

            const setor = setorSelect1.value; // Assumindo que setorSelect é um elemento do tipo select onde o usuário escolhe o setor
        
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
                       
                            // Decrementar ambos os setores Prim e Seg
                            let primSectorUrl = "http://localhost:8080/sectors/Prim" + setor + "/decrement";
                            let segSectorUrl = "http://localhost:8080/sectors/Seg" + setor + "/decrement";
        
                            // Decrementa Prim
                            const primDecrementResp = await fetch(primSectorUrl, {
                                method: "PATCH",
                                headers: { "Content-Type": "application/json" },
                            });
        
                            if (primDecrementResp.status === 200) {
                                console.log("Setor Prim atualizado com sucesso.");
                            } else {
                                console.log("Erro ao atualizar o setor Prim. Status: " + primDecrementResp.status, "warning");
                            }
        
                            // Decrementa Seg
                            const segDecrementResp = await fetch(segSectorUrl, {
                                method: "PATCH",
                                headers: { "Content-Type": "application/json" },
                            });
        
                            if (segDecrementResp.status === 200) {
                                console.log("Setor Seg atualizado com sucesso.");
                            } else {
                                console.log("Erro ao atualizar o setor Seg. Status: " + PrimDecrementResp.status, "warning");
                            }
                        
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
                    console.log("CPF não encontrado. Por favor, verifique seu CPF.", "warning");
                }
            } catch (error) {
                console.error("Erro ao buscar dados do usuário:", error);
                throw error; // Repassa o erro para tratamento externo
            }
        }
        
    
        async function updateSegReserva(cpf) {

            const setor = setorSelect2.value; // Assumindo que setorSelect é um elemento do tipo select onde o usuário escolhe o setor
        
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
                    let positionUrl = "http://localhost:8080/users/" + cpf + "/seg-reserva";
        
                    // Faz a requisição para atualizar a posição no método PUT
                    const resp = await fetch(positionUrl, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(setor),
                    });
        
                    if (resp.status === 200) {
                       
                            // Decrementar ambos os setores Prim e Seg
                            let primSectorUrl = "http://localhost:8080/sectors/Prim" + setor + "/decrement";
                            let segSectorUrl = "http://localhost:8080/sectors/Seg" + setor + "/decrement";
        
                            // Decrementa Prim
                            const primDecrementResp = await fetch(primSectorUrl, {
                                method: "PATCH",
                                headers: { "Content-Type": "application/json" },
                            });
        
                            if (primDecrementResp.status === 200) {
                                console.log("Setor Prim atualizado com sucesso.");
                            } else {
                                console.log("Erro ao atualizar o setor Prim. Status: " + segDecrementResp.status, "warning");
                            }
        
                            // Decrementa Seg
                            const segDecrementResp = await fetch(segSectorUrl, {
                                method: "PATCH",
                                headers: { "Content-Type": "application/json" },
                            });
        
                            if (segDecrementResp.status === 200) {
                                console.log("Setor Seg atualizado com sucesso.");
                            } else {
                                console.log("Erro ao atualizar o setor Seg. Status: " + PrimDecrementResp.status, "warning");
                            }
                                               }       
                       
                            const dequeueUrl = "http://localhost:8080/queues/Pass/dequeue";
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
                    console.log("CPF não encontrado. Por favor, verifique seu CPF.", "warning");
                }
            } catch (error) {
                console.error("Erro ao buscar dados do usuário:", error);
                throw error; // Repassa o erro para tratamento externo
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
    
})
});
