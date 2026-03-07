# Domain Model v1 — Server Local

Este documento define o contrato de modelagem do domínio Java para o V1.
Base de origem: `docs/DOMAIN_DISCOVERY.md`.

## Escopo V1

IN:
- `Sale` (Aggregate Root)
- `SaleItem`
- `Product`
- `Stock`
- `Money`, `Quantity`, `Percentage`, `PaymentMethod`

OUT:
- `CashRegister`
- cancelamento/estorno

---

## Aggregate Root

### Sale

`Sale` é o Aggregate Root de vendas e controla:
- consistência de itens
- cálculo de totais
- sequência de estados da venda
- validação de pagamento

Fica fora do aggregate:
- persistência
- HTTP/API
- integração externa

---

## Entidades e Contratos

### 1. Product

Identidade:
- `productId: ProductId`

Atributos mínimos:
- `name: String`
- `unitPrice: Money`

Invariantes:
- `name` não vazio
- `unitPrice > 0`

### 2. Stock

Identidade:
- coleção de saldo por `ProductId`

Atributos mínimos:
- `entries: Map<ProductId, Quantity>`

Invariantes:
- `ProductId` único
- nenhuma quantidade negativa
- produto vendido deve existir no estoque

### 3. SaleItem

Identidade:
- `productId: ProductId` (único dentro da venda)

Atributos mínimos:
- `productName: String` (snapshot)
- `unitPrice: Money` (snapshot)
- `quantity: Quantity`

Invariantes:
- `quantity > 0`
- `unitPrice > 0`

### 4. Sale

Identidade:
- `saleId: SaleId`

Atributos mínimos:
- `items: List<SaleItem>`
- `status: SaleStatus`
- `grossTotal: Money`
- `discountPercentage: Percentage` (opcional)
- `taxAmount: Money` (opcional)
- `finalTotal: Money` (após taxa)
- `paymentMethod: PaymentMethod` (após registro)
- `paidAmount: Money` (após quitação)
- `changeAmount: Money` (quando `CASH`)

Invariantes:
- venda sem item é inválida
- item duplicado por `ProductId` é inválido
- venda paga não pode ser alterada

---

## Value Objects (Contrato)

### ProductId
- identificador forte de produto (semântica de domínio)
- não pode ser nulo
- valor deve ser positivo

### SaleId
- identificador forte de venda (semântica de domínio)
- não pode ser nulo
- valor deve ser positivo

### Money
- base `BigDecimal`
- escala fixa 2
- arredondamento `HALF_UP`
- comparação via `compareTo`
- operações: `plus`, `minus`, `times`, `max`, `isNegative`, `isZero`

### Quantity
- inteiro positivo
- proíbe zero e negativo

### Percentage
- faixa fechada de `0` a `100`

### PaymentMethod
- enum: `CASH`, `DEBIT`, `CREDIT`

---

## Estados da Venda e Transições

Estados:
- `CREATED`
- `DISCOUNT_APPLIED`
- `TAX_APPLIED`
- `PAYMENT_REGISTERED`
- `PAID`

Transições válidas:
1. `CREATED -> DISCOUNT_APPLIED`
Pré-condição: venda com itens válidos.

2. `DISCOUNT_APPLIED -> TAX_APPLIED`
Pré-condição: desconto válido aplicado.

3. `TAX_APPLIED -> PAYMENT_REGISTERED`
Pré-condição: total final calculado.

4. `PAYMENT_REGISTERED -> PAID`
Pré-condição: pagamento válido conforme método.

Regra de proteção:
- qualquer transição fora da ordem gera erro de domínio.

---

## Regras Críticas do V1

1. `Stock` nunca pode ficar negativo.
2. `Sale` não pode finalizar sem pagamento válido.
3. `finalTotal` reflete desconto e taxa.
4. `Sale` em estado `PAID` é imutável.
5. todos os valores monetários usam `Money`.

---

## Diferenças ao sistema Python

1. Paradigma e estrutura
- Python original: funções procedurais com regras distribuídas.
- PDVjava: modelagem OO com Aggregate Root (`Sale`) e encapsulamento de invariantes.

2. Precisão monetária
- Python original: operações com `float`.
- PDVjava: `Money` com `BigDecimal`, escala fixa e política formal de arredondamento (ADR 0003).

3. Controle de estado da venda
- Python original: fluxo sequencial implícito.
- PDVjava: estados explícitos e transições protegidas.

4. Organização arquitetural
- Python original: menor isolamento entre regra e detalhe técnico.
- PDVjava: camadas com dependências para dentro.

5. Testabilidade
- Python original: regras menos formalizadas em testes.
- PDVjava: testes de domínio como documentação executável.

---

## Mapa de Migração (Legado -> Modelo V1)

- `carrinho` -> `Sale` em construção
- `item_carrinho` -> `SaleItem`
- `produto_id` -> `ProductId`
- `qtd` -> `Quantity`
- `preco` -> `Money`
- `metodo_pagamento` -> `PaymentMethod`
- `valor_pago` -> `paidAmount: Money`
- `resultado_venda` -> saída de caso de uso (application layer)

---

Este modelo pode evoluir por ADR e atualização incremental do roadmap.
