# Ace Motors — Contexto e Conceito do Projeto

> **Escopo deste documento:** ideia geral, contexto de negócio e conceito da marca. Não é específico de API nem de Web — serve como referência conceitual para os dois repositórios (`ace-motors-api` e `ace-motors-web`).

## 1. Sobre a Ace (grupo fictício)

A **Ace** é um grupo empresarial fictício de extremo luxo, criado como universo para projetos acadêmicos. O grupo possui diferentes divisões, cada uma vendendo um tipo de produto ou serviço, sempre dentro do posicionamento de luxo.

- **Ace Motors** (este projeto): divisão automotiva/mobilidade do grupo.
- *(Observação: existe outro projeto acadêmico, de outra disciplina, chamado **Ace Technology**, com itens de tecnologia à venda. Mencionado aqui apenas como contexto do universo "Ace" — não faz parte do escopo deste projeto.)*

## 2. Sobre a Ace Motors

A Ace Motors é a divisão automotiva do grupo Ace. Seu diferencial conceitual é vender **produtos que ainda não existem na realidade** — unindo o posicionamento de luxo extremo a uma proposta futurista.

Em vez de veículos convencionais, o catálogo da Ace Motors traz itens como:

- Skates flutuantes, que dispensam contato com o chão;
- Veículos totalmente autônomos, que dirigem sozinhos em qualquer situação, sem necessidade de motorista;
- Carros voadores, para deslocamento urbano vertical;
- Outros meios de transporte baseados em tecnologias ainda experimentais ou fictícias.

## 3. Público-alvo

Clientes de altíssimo poder aquisitivo e entusiastas de inovação/tecnologia, que buscam exclusividade e experiências de consumo únicas — fora do alcance do mercado automotivo tradicional.

## 4. Finalidade do sistema

Oferecer uma plataforma de e-commerce onde os produtos da Ace Motors possam ser apresentados e comprados pelos clientes, além de permitir que a administração da loja gerencie o catálogo, os pedidos e os clientes cadastrados.

## 5. Arquitetura geral do projeto

O projeto é dividido em **dois repositórios** (multirepo):

| Repositório | Responsabilidade |
|---|---|
| `ace-motors-api` | Backend — disponibiliza a API REST (Spring Boot) |
| `ace-motors-web` | Frontend — consome a API e exibe a interface ao usuário (Angular) |

Os requisitos funcionais e não-funcionais específicos de cada parte estão detalhados em arquivos separados (ver `01-requisitos-gerais.md`, `02-requisitos-api.md` e `03-requisitos-web.md`).
