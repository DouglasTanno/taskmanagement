# Task Manager
Sistema de gerenciamento de projetos e tarefas desenvolvido com Spring Boot e React.

## Tecnologias utilizadas
### Backend
- Java 21 
- Spring Boot 3.5.6
- Spring Security + JWT
- Spring Data JPA
- H2 Database
- JUnit + Mockito

### Frontend
- React 19.2.8
- Vite
- Material UI
- Axios

### Outras
- IDE IntelliJ 2026.2.0.1
  
## Como executar o projeto

Na pasta do backend: `mvn spring-boot:run`
ou, no IntelliJ, executar a classe `com.tanno.taskmanager.TaskManagerApplication`

A aplicação e a interface ficarão disponíveis em:

`http://localhost:8080`

O frontend foi compilado como aplicação estática e integrado pelo próprio Spring Boot, permitindo executar todo o sistema através de uma única aplicação, portanto não é necessário executar frontend e backend separadamente.

A documentação interativa da API está disponível através do Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

O Swagger permite visualizar os endpoints disponíveis, seus parâmetros, modelos de requisição e resposta, além de realizar testes diretamente pela interface.

Para acessar endpoints protegidos, utilize o botão **Authorize** no Swagger e informe o token JWT recebido no login no formato Bearer {token}.

O token pode ser obtido através do endpoint: `POST /auth/login`.

Após a autenticação, o mesmo token pode ser utilizado para testar os demais endpoints que exigem autorização.

Para facilitar a execução do projeto, foi injetado no backend o usuário ADMIN admin@taskmanager.com

O banco de dados H2 pode ser acessado em: 

`http://localhost:8080/h2-console`

## Usuários e permissões

O sistema possui dois níveis de acesso:
### USER
Permissões:
- Acessar projetos em que faz parte
- Criar tarefas em projetos que possui acesso
- Alterar o status das tarefas criadas por ele e/ou atribuídas a ele
- Editar tarefas criadas por ele e/ou atribuídas a ele
- Excluir tarefas criadas por ele

### ADMIN
Possui todas as permissões de USER. Permissões exclusivas:
- Criar projetos
- Editar projetos dos quais é owner
- Excluir projetos dos quais é owner
- Manter tarefas dos seus projetos
- Concluir tarefas críticas 

## Decisões técnicas e trade-offs
### 1. Autenticação com JWT

Foi utilizado JWT para autenticação, permitindo uma API stateless.

**Vantagens:**

Não necessita armazenar sessão no servidor
Fácil integração com aplicações frontend separadas

**Trade-off:**

Revogação de tokens exige uma estratégia adicional, como blacklist ou expiração curta.
Separação entre Controller, Service e Repository

### 2. A aplicação segue uma arquitetura em camadas:

Controller: responsável pelos endpoints HTTP
Service: concentra regras de negócio
Repository: acesso aos dados

**Vantagens:**

Código mais organizado
Facilita testes unitários
Evita regras espalhadas nos controllers

**Trade-off:**

Possui mais classes e abstrações para funcionalidades simples.

### 3. Validações de negócio no backend

As principais regras foram implementadas no backend:

- Usuário ADMIN necessário para gerenciamento de projetos
- Limite de 5 tarefas em andamento por usuário
- Tarefas TODO não podem ir diretamente para DONE e DONE não podem ir para TODO
- Tarefas CRITICAL só podem ser concluídas por ADMIN

O frontend também possui algumas validações para melhorar a experiência do usuário, porém o backend é considerado a fonte principal da regra.

### 4. Banco H2

Foi utilizado H2 para facilitar execução e avaliação do projeto.

**Vantagens:**

- Não necessita configuração externa
- Inicialização rápida

**Trade-off:**

Não representa completamente um ambiente de produção.

Em um ambiente real seria utilizado PostgreSQL ou outro banco relacional persistente.

## Evoluções futuras e melhorias

Devido à limitação de tempo de entrega do projeto, foi priorizado o desenvolvimento dos principais requisitos do backend.

Com mais tempo, algumas melhorias poderiam ser implementadas:

### Backend

- Implementação de recuperação de senha.
- Criação de um fluxo específico para cadastro de usuários ADMIN.
- Adição de validações mais robustas para senhas (ex.: tamanho mínimo, combinação de caracteres e requisitos de segurança).
- Implementação de validações de e-mail mais completas.
- Padronização das mensagens de erro entre backend e frontend.
- Substituição do banco H2 por um banco relacional persistente, como PostgreSQL, para uso em produção.

### Frontend

- Implementação de drag and drop no quadro de tarefas para alterar o status das tarefas de forma mais intuitiva.
- Exibição do usuário responsável pela tarefa nos cards das tarefas, apenas para visualização.
- Exibição do criador dos projetos.
- Apresentação de informações adicionais nos cards, como data de criação, data de atualização e deadline das tarefas.
