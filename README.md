# 🎸 Groove Festival System

Groove Festival System é uma aplicação web fullstack que simula a venda de ingressos para um evento fictício de dois dias, incorporando controle de acesso por fila, reservas por setor e notificações automatizadas.

O sistema foi projetado para aplicar estruturas de dados clássicas (fila e pilha) em um contexto realista, simulando cenários de alta demanda e acesso limitado.

---

## 🚀 Funcionalidades

- Landing page interativa para um evento fictício
- Sistema de fila para acesso antecipado a ingressos
- Diferentes tipos de ingresso:
  - **Day** (1 dia)
  - **Pass** (2 dias)
  - **VIP** (acesso imediato, sem fila)
- Escolha de setores:
  - Pista
  - Pista Premium
  - Camarote
- Visualização da posição na fila
- Possibilidade de saída da fila
- Envio automático de e-mails quando o usuário chega ao topo da fila
- Controle de acesso via token seguro
- Reserva de ingressos com controle de disponibilidade

---

## 🧠 Conceitos Técnicos Aplicados

### 🔹 Controle de Acesso com Fila (FIFO)
- Implementação de múltiplas filas em memória
- Gerenciamento por tipo de ingresso
- Simulação de alta demanda com capacidade limitada
- Processamento sequencial com prioridade por ordem de entrada

### 🔹 Sistema de Reservas com Pilha (LIFO)
- Controle de ingressos disponíveis por setor
- Estrutura baseada em pilha para gerenciamento de reservas
- Integração com persistência em banco de dados

### 🔹 Processamento Assíncrono
- Uso de `ScheduledExecutorService` para controle de tempo
- Janela de 2 horas para seleção de ingressos
- Liberação automática do próximo usuário na fila

### 🔹 Autenticação e Segurança
- Geração de tokens para acesso a páginas protegidas
- Evita exposição direta de dados sensíveis (CPF)

### 🔹 Arquitetura Híbrida
- Persistência com banco de dados (JPA)
- Estruturas de dados em memória para performance

---

## 🏗️ Arquitetura

### Backend
- Java + Spring Boot
- Spring Data JPA
- Spring Mail
- Sistema de tokens para autenticação

### Frontend
- HTML, CSS, JavaScript
- Bootstrap
- Manipulação dinâmica de DOM
- Integração com API via Fetch

---

## 🔄 Fluxo do Sistema

1. Usuário se cadastra para ingresso
2. Sistema adiciona o usuário na fila (exceto VIP)
3. Usuário pode:
   - Consultar posição
   - Sair da fila
4. Quando chega ao topo:
   - Recebe e-mail automático
   - Recebe link com token seguro
5. Usuário acessa página protegida
6. Seleciona setor e reserva ingresso
7. Sistema atualiza disponibilidade

---

## ⚙️ Validações e Regras de Negócio

- Validação completa de CPF no frontend
- VIP não entra na fila
- Diferentes fluxos para cada tipo de ingresso
- Tempo limite para reserva
- Atualização de estado do usuário (ativo/inativo)

---

## 💡 Destaques Técnicos

- Integração entre eventos de sistema (fila → email → ação do usuário)
- Aplicação prática de estruturas de dados em sistema real
- Simulação de controle de acesso sob demanda
- Separação de responsabilidades entre camadas
- Manipulação de estado em frontend e backend

---

## 🔧 Possíveis Melhorias

- Persistência completa das filas (atualmente em memória)
- Uso de mensageria (RabbitMQ/Kafka) para eventos
- Escalonamento do scheduler
- Internacionalização
- Deploy em nuvem

---

## 📌 Objetivo do Projeto

Este projeto foi desenvolvido com foco educacional, com o objetivo de aplicar estruturas de dados clássicas em um cenário prático, simulando sistemas reais de venda de ingressos com controle de acesso e concorrência.
