# Ace Motors — Requisitos do Web (ace-motors-web)

> **Escopo deste documento:** requisitos funcionais e não-funcionais específicos do repositório **frontend** (`ace-motors-web`).
>
> ⚠️ Documento ainda em revisão — itens marcados como "a confirmar" dependem de validação sua antes de serem considerados definitivos.

## Stack confirmada

- **Framework:** Angular
- **Consumo de dados:** via API REST (`ace-motors-api`), comunicação HTTP/JSON
- **Execução em desenvolvimento:** `ng serve` (hot-reload local)
- **Build de produção:** `ng build`, arquivos estáticos servidos via Nginx (container) no ambiente de apresentação

## Paleta de cores

| Cor | Hex | Uso |
|---|---|---|
| Preto | `#121212` | Cor de base / fundo principal |
| Branco | `#FAFAFA` | Cor secundária |
| Azul profundo | `#0A1930` | Cor de identidade/refino da marca — **não necessariamente usada como cor de fundo da UI**, funciona mais como detalhe/acento que reforça a identidade visual |

## Requisitos Funcionais (RF) — telas e fluxos

| ID | Requisito |
|---|---|
| RF-W01 | Tela de **catálogo de produtos**, com busca e filtros (categoria, faixa de preço, tipo de tecnologia). |
| RF-W02 | Tela de **detalhe do produto**, incluindo exibição das **avaliações** feitas por outros clientes e opção de o cliente logado avaliar o produto. |
| RF-W03 | Fluxo de **cadastro e login** de clientes. |
| RF-W04 | Fluxo de **recuperação de senha** (solicitação de token/link de recuperação). |
| RF-W05 | Tela de **carrinho de compras**. |
| RF-W06 | Fluxo de **checkout/finalização de pedido**, incluindo: aplicação de **cupom de desconto** e seleção entre **múltiplos métodos de pagamento** (PIX, cartão). Checkout disponível apenas para clientes autenticados. |
| RF-W07 | Tela de **histórico de pedidos** do cliente. |
| RF-W08 | **Painel administrativo**, com telas para: cadastro/edição/remoção de produtos (incluindo upload de imagem), gestão de clientes e visualização de relatórios de vendas. |

## Requisitos Não-Funcionais (RNF) específicos do Web

| ID | Requisito |
|---|---|
| RNF-W01 | Interface deve ser **responsiva**, adaptando-se a desktop e mobile. |
| RNF-W02 | Interface deve seguir a **paleta de cores definida**, mantendo consistência visual em todas as telas. |
