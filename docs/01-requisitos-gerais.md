# Ace Motors — Requisitos Gerais (compartilhados)

> **Escopo deste documento:** requisitos que não pertencem exclusivamente a um dos dois repositórios — valem tanto para `ace-motors-api` quanto para `ace-motors-web`.
>
> ⚠️ Documento ainda em revisão — itens marcados como "a confirmar" dependem de validação sua antes de serem considerados definitivos.

## Requisitos Não-Funcionais (RNF) Gerais

| ID | Requisito |
|---|---|
| RNF01 | O sistema deve adotar uma arquitetura com **backend responsável por disponibilizar uma API REST** e **frontend separado responsável por consumir essa API**, comunicando-se via HTTP/JSON. |
| RNF02 | O banco de dados deve ser versionado via **Flyway**, com migrations controladas. |
| RNF03 | A API deve ser testada e documentada utilizando **Postman**. |
| RNF04 | Tempo de resposta das requisições deve ser aceitável para um ambiente de demonstração/acadêmico (sem exigência de otimização para alta concorrência). |

## Ambiente de Desenvolvimento e Deploy

| ID | Requisito |
|---|---|
| RNF05 | **Ambiente de desenvolvimento local:** banco de dados MySQL executado via Docker (`docker-compose`); backend (Spring Boot) e frontend (Angular) executados localmente, fora de container, para permitir hot-reload. |
| RNF06 | **Ambiente de apresentação/entrega final:** aplicação completa conteinerizada via Docker e implantada em servidor próprio (Ubuntu Server). O backend é compilado em `.jar` e rodado em container; o frontend é compilado para produção (`ng build`) e seus arquivos estáticos servidos via um container Nginx; o MySQL continua em container. |

## Pagamentos — observação geral

| ID | Requisito |
|---|---|
| RNF07 | Na versão inicial do projeto, a verificação de métodos de pagamento (cartão, PIX) deve ser **genérica/simulada**, sem integração real com instituições financeiras. Integração real com banco/gateway de pagamento é uma funcionalidade **futura, apenas se houver tempo hábil** (não faz parte do escopo obrigatório). Detalhes funcionais estão em `02-requisitos-api.md`. |
