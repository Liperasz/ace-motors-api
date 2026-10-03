# Ace Motors — Modelagem do Banco de Dados

> **Escopo deste documento:** descrição das tabelas, atributos e relacionamentos do banco de dados da Ace Motors, de forma legível. O schema técnico equivalente (para importar no dbdiagram.io) está em `ace-motors-schema.dbml`.
>
> ⚠️ Documento em revisão — alguns pontos estão marcados como pendentes de confirmação.

---

## 1. Pessoa, Cliente e Administrador

O sistema usa uma tabela base **Pessoa**, da qual **Cliente** e **Administrador** herdam. Essa herança é implementada no banco por chave primária compartilhada (o `id` de Cliente e de Administrador é o mesmo `id` da Pessoa correspondente) — no Java, isso corresponde a `Cliente` e `Administrador` estendendo uma classe `Pessoa`.

### Pessoa
Dados comuns a qualquer pessoa do sistema, seja cliente ou administrador.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| nome | Nome completo |
| email | E-mail, único no sistema — usado como login |
| senha_hash | Senha armazenada com hash (nunca em texto plano) |
| created_at / updated_at | Datas de criação e última atualização |

### Cliente
Estende Pessoa. Guarda apenas os dados que são específicos de quem compra na loja.

| Atributo | Descrição |
|---|---|
| id | Mesmo valor do id da Pessoa correspondente |
| status | `ativo` ou `bloqueado` |

**Relacionamento:** 1 Pessoa → 1 Cliente (herança).

### Administrador
Estende Pessoa. Por enquanto não tem campos próprios além do vínculo com Pessoa — é essencialmente um "marcador" indicando que aquela pessoa tem acesso ao painel administrativo. 📌 *Se precisar de campo extra (ex: nível de acesso), pode ser adicionado depois.*

**Relacionamento:** 1 Pessoa → 1 Administrador (herança).

### Histórico de status do Cliente
Registra cada mudança de status do cliente (ex: bloqueado por fraude, depois desbloqueado).

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| cliente_id | Cliente afetado |
| status_anterior | Status antes da mudança |
| status_novo | Status depois da mudança |
| alterado_por_admin_id | Administrador que fez a mudança (vazio se foi automático, pelo sistema) |
| motivo | Texto livre explicando a mudança |
| data_alteracao | Quando ocorreu |

**Relacionamento:** cada Cliente pode ter vários registros de histórico de status.

---

## 2. Endereço e Telefone

Ambos pertencem à **Pessoa** (não a Cliente ou Administrador especificamente), e seguem o mesmo padrão: uma pessoa pode ter vários, cada um pertence a uma única pessoa (1:N).

### Endereço

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pessoa_id | Dono do endereço |
| logradouro, numero, complemento, bairro, cidade, estado, cep | Campos padrão de endereço brasileiro |
| created_at | Data de cadastro |

### Telefone

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pessoa_id | Dono do telefone |
| numero | Número de telefone |
| created_at | Data de cadastro |

📌 *Pendente de confirmação: o relacionamento é 1 Pessoa → N Endereços (uma pessoa pode ter vários endereços, cada endereço pertence só a ela). Você mencionou de outra forma em um ponto da conversa ("um endereço pode ser de mais de um cliente") — segui o 1:N por ser o que ficou confirmado no exemplo do telefone, mas se a intenção era endereço compartilhado entre clientes diferentes, isso muda a modelagem.*

---

## 3. Recuperação de senha

Tabela de apoio para o fluxo de "esqueci minha senha", vinculada a Pessoa (serve tanto para Cliente quanto Administrador).

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pessoa_id | Pessoa que solicitou a recuperação |
| token | Código/token único gerado |
| usado | Se o token já foi utilizado |
| expira_em | Validade do token |
| created_at | Quando foi gerado |

---

## 4. Catálogo (Categoria e Produto)

### Categoria

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| nome | Nome da categoria |
| descricao | Descrição da categoria |

### Produto

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| nome | Nome do produto |
| descricao | Descrição do produto |
| preco | Preço atual |
| imagem_url | Caminho da imagem (WebP) salva no servidor |
| status | `ativo`, `inativo` ou `esgotado` |
| created_at / updated_at | Datas de criação e última atualização |

**Relacionamento Produto ↔ Categoria:** muitos-para-muitos — um produto pode pertencer a mais de uma categoria, e uma categoria pode ter vários produtos. Isso é resolvido por uma tabela de associação (`produto_categoria`) que só guarda os pares produto/categoria.

### Histórico de status do Produto
Mesmo princípio do histórico de cliente, aplicado ao produto (ex: produto que ficou esgotado e depois voltou ao estoque).

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| produto_id | Produto afetado |
| status_anterior | Status antes da mudança |
| status_novo | Status depois da mudança |
| alterado_por_admin_id | Administrador responsável (vazio se automático) |
| motivo | Texto livre |
| data_alteracao | Quando ocorreu |

### Favorito
Relação entre Cliente e Produto — permite ao cliente salvar produtos para ver depois.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| cliente_id | Cliente que favoritou |
| produto_id | Produto favoritado |
| created_at | Quando foi favoritado |

**Relacionamento:** muitos-para-muitos entre Cliente e Produto (um cliente favorita vários produtos; um produto pode ser favoritado por vários clientes), resolvido por essa tabela de associação. Cada cliente só pode favoritar o mesmo produto uma vez.

---

## 5. Carrinho de compras

### Carrinho
Cada cliente tem exatamente um carrinho.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| cliente_id | Dono do carrinho (único — um carrinho por cliente) |
| created_at | Data de criação |

### Item do Carrinho
Produtos dentro do carrinho, com a quantidade desejada.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| carrinho_id | Carrinho a que pertence |
| produto_id | Produto adicionado |
| quantidade | Quantidade desejada |
| created_at | Quando foi adicionado |

**Relacionamento:** um Carrinho tem vários Itens de Carrinho; cada item aponta para um Produto. Um mesmo produto não se repete em duas linhas no mesmo carrinho (a quantidade é atualizada na mesma linha).

> A lógica de "comprar só alguns itens selecionados" ou "comprar tudo de uma vez" não exige tabela extra — é tratada na aplicação: ao finalizar a compra, os itens selecionados do carrinho são transformados em Itens de Pedido, e opcionalmente removidos do carrinho.

---

## 6. Cupom de desconto

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| codigo | Código do cupom (ex: "ACE10"), único |
| tipo | `percentual` ou `valor_fixo` |
| valor | Valor do desconto (percentual ou fixo, conforme o tipo) |
| data_inicio / data_expiracao | Janela de validade |
| uso_maximo | Limite de usos permitidos |
| uso_atual | Quantas vezes já foi usado |
| ativo | Se está disponível para uso |

**Relacionamento:** um Cupom pode ser usado por vários Pedidos (ao longo do tempo, respeitando o limite de uso); cada Pedido usa no máximo um Cupom.

---

## 7. Pedido

O Pedido é dividido em três partes, para equilibrar praticidade (referências vivas) e integridade histórica (dados que não podem mudar depois):

### Pedido (estado atual)
Guarda o pedido em si, com referências "vivas" para outras tabelas.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| cliente_id | Cliente que fez o pedido |
| endereco_id | Endereço de entrega (referência ao endereço cadastrado do cliente) |
| cupom_id | Cupom aplicado (opcional) |
| status | `aguardando_pagamento`, `pago`, `enviado`, `entregue` ou `cancelado` |
| valor_subtotal | Soma dos itens, sem desconto |
| valor_desconto | Valor abatido pelo cupom |
| valor_total | Valor final |
| created_at / updated_at | Datas de criação e última atualização |

### Histórico do Pedido (snapshot imutável)
Guarda uma cópia fixa de dados sensíveis do pedido, tirada no momento da finalização da compra — assim, se o cliente editar o endereço cadastrado depois, o pedido antigo continua mostrando o endereço de quando a compra foi feita.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pedido_id | Pedido correspondente (um snapshot por pedido) |
| endereco_snapshot | Texto completo do endereço, copiado no momento da compra |
| valor_total_snapshot | Valor total, copiado no momento da compra |
| data_snapshot | Quando o snapshot foi tirado |

### Histórico de status do Pedido
Mesmo princípio dos outros históricos de status — rastreia a linha do tempo do pedido (quando passou de "aguardando pagamento" para "pago", etc.).

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pedido_id | Pedido afetado |
| status_anterior | Status antes da mudança |
| status_novo | Status depois da mudança |
| alterado_por_admin_id | Administrador responsável (vazio se automático, ex: confirmação de pagamento) |
| observacao | Texto livre |
| data_alteracao | Quando ocorreu |

### Item do Pedido
Produtos que compõem o pedido.

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pedido_id | Pedido a que pertence |
| produto_id | Produto comprado |
| quantidade | Quantidade comprada |
| preco_unitario | Preço do produto **no momento da compra** (não muda se o preço do produto mudar depois) |
| subtotal | quantidade × preco_unitario |

**Relacionamento Pedido ↔ Produto:** muitos-para-muitos — um pedido pode ter vários produtos, e um produto pode aparecer em vários pedidos diferentes — resolvido pela tabela Item do Pedido, que também carrega a quantidade e o preço da época.

---

## 8. Avaliação

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| item_pedido_id | Item de pedido avaliado (único — uma avaliação por item de pedido) |
| nota | Nota de 1 a 5 |
| comentario | Comentário opcional |
| created_at | Data da avaliação |

**Relacionamento:** cada Avaliação está ligada a um Item de Pedido específico (não diretamente a Produto + Cliente). Isso garante, pela própria estrutura do banco:
- só é possível avaliar um produto que realmente foi comprado (porque precisa existir o Item de Pedido correspondente);
- o cliente não consegue avaliar o mesmo produto duas vezes dentro da mesma compra;
- se o cliente comprar o mesmo produto de novo, em um pedido novo, um novo Item de Pedido é criado, e isso libera uma nova avaliação.

📌 *A regra de "só pode avaliar depois que o pedido foi entregue" não dá pra garantir só com a estrutura do banco — isso precisa ser validado pela aplicação (checar se o status do pedido é "entregue" antes de aceitar a avaliação).*

---

## 9. Forma de Pagamento e Pagamento

Duas tabelas com propósitos diferentes:
- **Forma de Pagamento:** um método salvo pelo cliente para reutilizar depois (ex: um cartão cadastrado).
- **Pagamento:** o registro real de uma tentativa/transação de pagamento, ligada a um pedido específico.

### Forma de Pagamento

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| cliente_id | Dono da forma de pagamento |
| tipo | `pix`, `cartao_credito` ou `cartao_debito` |
| apelido | Nome dado pelo cliente (ex: "Cartão principal") |
| dados_mascarados | Dado fictício/genérico (ex: "**** **** **** 1234"), sem integração bancária real |
| created_at | Data de cadastro |

**Relacionamento:** um Cliente pode ter várias Formas de Pagamento salvas.

### Pagamento

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pedido_id | Pedido ao qual se refere |
| forma_pagamento_id | Forma de pagamento salva usada (opcional — o cliente pode pagar sem usar uma forma salva) |
| metodo | `pix`, `cartao_credito` ou `cartao_debito` |
| status | `pendente`, `aprovado`, `recusado` ou `estornado` |
| valor | Valor da transação |
| codigo_transacao | Código PIX fictício ou identificador simulado de cartão |
| created_at / updated_at | Datas de criação e última atualização |

**Relacionamento:** um Pedido tem um Pagamento associado (podendo haver mais de uma tentativa, caso a primeira seja recusada).

### Histórico de status do Pagamento
Mesmo princípio dos outros históricos — rastreia a transição de status do pagamento (ex: pendente → aprovado → estornado).

| Atributo | Descrição |
|---|---|
| id | Identificador único |
| pagamento_id | Pagamento afetado |
| status_anterior | Status antes da mudança |
| status_novo | Status depois da mudança |
| alterado_por_admin_id | Administrador responsável (vazio se automático) |
| data_alteracao | Quando ocorreu |

---

## 10. Resumo dos relacionamentos muitos-para-muitos (N:N)

| Relação | Tabela de associação | Dados extras na associação |
|---|---|---|
| Produto ↔ Categoria | produto_categoria | — |
| Cliente ↔ Produto (favoritos) | favorito | — |
| Carrinho ↔ Produto | item_carrinho | quantidade |
| Pedido ↔ Produto | item_pedido | quantidade, preço da época |

## 11. Pontos ainda pendentes de confirmação

- Relacionamento Endereço ↔ Pessoa: confirmar se é 1:N (como modelado) ou se há intenção de endereço compartilhado entre clientes diferentes.
- Campos específicos do Administrador (além da herança de Pessoa) — hoje não tem nenhum campo próprio.
- Se será necessário histórico/auditoria para Endereço, Telefone, Forma de Pagamento ou Favorito (hoje nenhuma dessas tem tabela de histórico).
