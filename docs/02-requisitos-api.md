# Ace Motors — Requisitos da API (ace-motors-api)

> **Escopo deste documento:** requisitos funcionais e não-funcionais específicos do repositório **backend** (`ace-motors-api`).
>
> ⚠️ Documento ainda em revisão — itens marcados como "a confirmar" dependem de validação sua antes de serem considerados definitivos.

## Stack confirmada

- **Linguagem/Framework:** Java 17 + Spring Boot
- **Persistência:** Spring Data JPA
- **Banco de dados:** MySQL
- **Migrations:** Flyway (a ser adicionado quando as entidades iniciais estiverem definidas)
- **Autenticação:** JWT (Spring Security, a ser adicionado junto da implementação do JWT)
- **Validação:** Spring Validation
- **Testes de API:** Postman
- **Build tool:** Maven
- **Dependências já decididas no setup inicial:** Spring Web, Spring Data JPA, MySQL Driver, Validation, Lombok, Spring Boot DevTools

## Entidades previstas

Entidades necessárias para suportar as funcionalidades aprovadas (📌 nomes/campos exatos podem ser refinados na modelagem):

- **Produto**
- **Categoria**
- **Cliente**
- **Pedido**
- **Item do Pedido**
- **Avaliação** (review de produto feita por cliente)
- **Cupom** (cupom de desconto)
- **Pagamento** (registro do método e status de uma tentativa de pagamento de um pedido)

## Requisitos Funcionais (RF)

| ID | Requisito |
|---|---|
| RF01 | A API deve expor endpoints de **catálogo de produtos**, com suporte a busca e filtros (categoria, faixa de preço, tipo de tecnologia). |
| RF02 | A API deve expor endpoints de **cadastro e autenticação de clientes**, com login via JWT. |
| RF03 | A API deve expor endpoints de **carrinho de compras**. |
| RF04 | A API deve expor endpoints de **finalização de pedido (checkout)**. Apenas clientes cadastrados e autenticados podem finalizar uma compra (não há checkout como convidado). |
| RF05 | A API deve expor endpoints administrativos para **cadastro, edição e remoção de produtos**. |
| RF06 | A API deve expor endpoints administrativos para **gestão de clientes**. |
| RF07 | A API deve expor endpoints administrativos para **relatórios de vendas**. |
| RF08 | A API deve expor endpoint de **histórico de pedidos** do cliente autenticado. |
| RF09 | A API deve permitir que clientes **avaliem produtos** (avaliação/review), associada ao produto e ao cliente. |
| RF10 | A API deve permitir a aplicação de **cupons de desconto** no checkout. 📌 *A confirmar: regras de criação do cupom (percentual vs. valor fixo), validade/expiração, limite de uso — definir junto com a modelagem.* |
| RF11 | A API deve expor fluxo de **recuperação de senha**: geração de token de recuperação, com simulação do envio (token exibido/registrado em log/console, sem envio real de e-mail). 📌 *Funcionalidade futura, apenas se houver tempo: envio real via SMTP.* |
| RF12 | A API deve expor endpoints para **múltiplos métodos de pagamento** (PIX e cartão), com verificação **genérica/simulada**: validação de formato de cartão (sem comunicação com operadora real) e geração de código PIX fictício (string simulando o "copia e cola"). 📌 *Funcionalidade futura, apenas se houver tempo: integração real com banco/gateway de pagamento.* |

## Requisitos Não-Funcionais (RNF) específicos da API

### Segurança

| ID | Requisito |
|---|---|
| RNF-A01 | Todas as rotas da API, exceto login/registro e catálogo público, devem exigir token **JWT** válido. |
| RNF-A02 | Senhas devem ser armazenadas com **hash (BCrypt)**, nunca em texto plano. |
| RNF-A03 | Tokens JWT devem ter **tempo de expiração** definido. |

### Armazenamento de imagens dos produtos

| ID | Requisito |
|---|---|
| RNF-A04 | Imagens de produtos devem ser enviadas no formato **WebP**. |
| RNF-A05 | O arquivo da imagem deve ser salvo em uma **pasta no servidor** (ex: `/uploads/produtos/`); o MySQL deve armazenar **apenas o caminho/URL** da imagem (não a imagem em Base64). |
| RNF-A06 | O upload da imagem (feito pelo admin) deve ser recebido via **multipart/form-data**. A leitura da imagem (pelo cliente) deve ser feita via **URL de arquivo estático** exposta pela API — o JSON do produto carrega apenas o campo com a URL, não o binário da imagem. |

### Cache e Rate Limit

| ID | Requisito |
|---|---|
| RNF-A07 | *(Se der tempo)* Implementar **cache HTTP simples** (ex: headers `Cache-Control`) na entrega das imagens de produto. |
| RNF-A08 | *(Se der tempo)* Implementar **rate limiting básico** na API. |
