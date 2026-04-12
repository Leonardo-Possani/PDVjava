# Domain Model v1 — Server Local

Este documento define o contrato de modelagem do domínio Java para o V1.
Base de origem: `docs/DOMAIN_DISCOVERY.md`.

## Escopo V1

IN:
- `Cart` (Aggregate Root)
- `Sale` (Aggregate Root)
- `SaleItem`
- `Product`
- `Stock`
- `Money`, `Quantity`, `Percentage`, `PaymentMethod`

OUT:
- `CashRegister`
- cancelamento/estorno

---

## Aggregate Roots

### Cart

`Cart` é o Aggregate Root de montagem da venda e controla:
- consistência dos itens em edição
- soma incremental de quantidade por `ProductId`
- cálculo do total corrente do carrinho
- bloqueio de edição quando o processo de venda é iniciado
- persistência e recuperação do estado editável do carrinho

Fica fora do aggregate:
- pagamento
- baixa de estoque
- HTTP/API
- integração externa

### Sale

`Sale` é o Aggregate Root transacional criado a partir de um `Cart` válido e controla:
- consistência final dos itens
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
- `productId` não pode ser nulo
- `name` não pode ser nulo
- `name` não pode ser vazio/em branco após normalização com `trim()`
- `name` é armazenado já normalizado sem espaços nas extremidades
- `unitPrice > 0`
- `unitPrice` não pode ser nulo

Contrato implementado no `server-local`:
- criação via `Product.of(ProductId productId, String name, Money unitPrice)`
- classe imutável com leitura por:
  - `productId()`
  - `name()`
  - `unitPrice()`
- mensagens de erro atuais:
  - `DomainValidationException("product id cannot be null")`
  - `DomainValidationException("product name cannot be null")`
  - `DomainValidationException("product name cannot be blank")`
  - `DomainValidationException("product unit price cannot be null")`
  - `DomainValidationException("product unit price cannot be zero")`
  - `DomainValidationException("product unit price cannot be negative")`

### 2. Stock

Identidade:
- coleção de saldo por `ProductId`

Atributos mínimos:
- `entries: Map<ProductId, StockBalance>`

Invariantes:
- `Stock` recebe entradas já normalizadas por `ProductId`
- nenhuma quantidade negativa
- produto vendido deve existir no estoque

Contrato implementado no `server-local`:
- criação via `Stock.of(Map<ProductId, StockBalance> entries)`
- classe imutável com leitura por:
  - `contains(ProductId productId)`
  - `balanceOf(ProductId productId)`
  - `decrease(ProductId productId, Quantity quantity)`
- `decrease` retorna nova instância de `Stock`
- a unicidade de `ProductId` é garantida antes da materialização do domínio, na persistência e na infraestrutura que monta o `Map`
- mensagens de erro atuais:
  - `DomainValidationException("stock entries cannot be null")`
  - `DomainValidationException("stock product id cannot be null")`
  - `DomainValidationException("stock balance cannot be null")`
  - `DomainValidationException("product not found in stock")`
  - `DomainValidationException("quantity cannot be null")`

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
- `productId` não pode ser nulo
- `productName` não pode ser nulo
- `productName` não pode ser vazio/em branco após normalização com `trim()`
- `productName` é armazenado já normalizado sem espaços nas extremidades
- `unitPrice` não pode ser nulo
- `quantity` não pode ser nula

Contrato implementado no `server-local`:
- criação via `SaleItem.of(ProductId productId, String productName, Money unitPrice, Quantity quantity)`
- classe imutável com leitura por:
  - `productId()`
  - `productName()`
  - `unitPrice()`
  - `quantity()`
  - `lineTotal()`
- `lineTotal` é calculado internamente por `unitPrice.times(quantity)`
- mensagens de erro atuais:
  - `DomainValidationException("sale item product id cannot be null")`
  - `DomainValidationException("sale item product name cannot be null")`
  - `DomainValidationException("sale item product name cannot be blank")`
  - `DomainValidationException("sale item unit price cannot be null")`
  - `DomainValidationException("sale item unit price cannot be zero")`
- `DomainValidationException("sale item unit price cannot be negative")`
- `DomainValidationException("sale item quantity cannot be null")`

### 4. Cart

Identidade:
- `cartId: CartId`

Atributos mínimos:
- `items: List<SaleItem>`
- `status: CartStatus`
- `totalAmount: Money`

Invariantes:
- `cartId` não pode ser nulo
- carrinho pode existir vazio enquanto está editável
- item no carrinho é identificado por `ProductId`
- ao adicionar item com `ProductId` já existente, o carrinho deve somar a `quantity` ao item existente em vez de duplicar a linha
- `totalAmount` é sempre calculado internamente como soma de `lineTotal()` dos itens atuais
- carrinho editável pode ser persistido e recuperado para continuar edição
- carrinho iniciado para venda torna-se imutável
- carrinho imutável não aceita adição, remoção ou alteração de item

Contrato alvo V1:
- criação via `Cart.of(CartId cartId)`
- classe de domínio com leitura por:
  - `cartId()`
  - `items()`
  - `status()`
  - `totalAmount()`
- operações mínimas esperadas:
  - `addItem(SaleItem saleItem)`
  - `removeItem(ProductId productId)`
  - `startSale()`
- comportamento obrigatório:
  - `addItem` soma quantidade quando o `ProductId` já existe no carrinho
  - `startSale` exige `Cart` válido para venda e bloqueia novas mutações
  - `startSale` é o ponto de transição para o fluxo transacional que originará `Sale`

Contrato implementado no `server-local` até o momento:
- `CartId` implementado como Value Object com factory `CartId.of(Long value)`, leitura por `value()` e igualdade semântica por valor
- `CartStatus` implementado com estados:
  - `EDITABLE`
  - `CHECKOUT_STARTED`
- `Cart` implementado parcialmente com:
  - criação via `Cart.of(CartId cartId)`
  - leitura por `cartId()`, `items()`, `status()` e `totalAmount()`
  - `Cart` nasce vazio, com `totalAmount = 0.00` e `status = EDITABLE`
  - `addItem(SaleItem saleItem)` já implementado para inclusão de item novo em carrinho editável
- próximo ciclo pendente em `Cart`:
  - somar quantidade quando o mesmo `ProductId` for adicionado novamente
  - remover item por `ProductId`
  - recalcular total com merge e remoção
  - iniciar checkout com bloqueio de mutação

### 5. Sale

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
- `Sale` nasce a partir de um `Cart` válido já bloqueado para edição
- venda paga não pode ser alterada

---

## Value Objects (Contrato)

### ProductId
- identificador forte de produto (semântica de domínio)
- não pode ser nulo
- valor deve ser positivo
- base `Long`
- criação via factory `ProductId.of(Long value)`
- expõe `value()` para leitura do identificador
- igualdade semântica baseada no valor do identificador
- mensagens de erro atuais:
  - `DomainValidationException("product id cannot be null")`
  - `DomainValidationException("product id must be greater than zero")`

### CartId
- identificador forte de carrinho (semântica de domínio)
- não pode ser nulo
- valor deve ser positivo
- criação via factory `CartId.of(Long value)`
- expõe `value()` para leitura do identificador
- igualdade semântica baseada no valor do identificador

### CartStatus
- enum de estados explícitos do carrinho
- estados alvo do V1:
  - `EDITABLE`
  - `CHECKOUT_STARTED`

### SaleId
- identificador forte de venda (semântica de domínio)
- não pode ser nulo
- valor deve ser positivo
- criação via factory `SaleId.of(Long value)`
- expõe `value()` para leitura do identificador
- igualdade semântica baseada no valor do identificador
- mensagens de erro atuais:
  - `DomainValidationException("sale id cannot be null")`
  - `DomainValidationException("sale id must be greater than zero")`

### SaleStatus
- enum de estados explícitos da venda
- estados implementados no `server-local`:
  - `OPEN`
  - `PAID`

### Money
- base `BigDecimal`
- escala fixa 2
- arredondamento `HALF_UP`
- comparação via `compareTo`
- operações: `plus(Money)`, `minus(Money)`, `times(Quantity)`, `max(Money)`, `isNegative`, `isZero`
- expõe `value()` para leitura do valor monetário já normalizado

### Quantity
- representa quantidade inteira de item
- base `int`
- criação via factory `Quantity.of(int value)`
- aceita apenas valores maiores que zero
- proíbe zero e negativo
- expõe `value()` para leitura do valor normalizado
- implementa `compareTo`
- comparar com `null` deve gerar `DomainValidationException`
- mensagem de erro atual na criação inválida:
  `DomainValidationException("quantity must be greater than zero")`

### StockBalance
- representa saldo de estoque por produto
- base `int`
- criação via factory `StockBalance.of(int value)`
- aceita `0` e valores positivos
- rejeita valor negativo
- expõe `value()` para leitura do saldo atual
- implementa `compareTo`
- operações:
  - `plus(Quantity quantity)`
  - `minus(Quantity quantity)`
  - `isZero()`
- comparar com `null` deve gerar `DomainValidationException`
- mensagens de erro atuais:
  - `DomainValidationException("stock balance cannot be negative")`
  - `DomainValidationException("quantity cannot be null")`
  - `DomainValidationException("stock balance cannot be negative after subtraction")`
  - `DomainValidationException("stock balance to compare cannot be null")`

### Percentage
- faixa fechada de `0` a `100`
- base `BigDecimal`
- escala fixa 2
- arredondamento `HALF_UP`
- criação via factory `Percentage.of(BigDecimal percentage)`
- `null` é inválido
- valores são normalizados para 2 casas decimais no momento da criação
- limites válidos incluem `0.00` e `100.00`
- valores menores que `0` ou maiores que `100` são inválidos
- expõe `value()` para leitura do valor normalizado
- implementa `compareTo`
- comparar com `null` deve gerar `DomainValidationException`
- mensagens de erro atuais:
  - `DomainValidationException("percentage cannot be null")`
  - `DomainValidationException("discount percentage out of range")`

### Observações de Contrato dos VOs Já Implementados

`Money`, `Quantity`, `Percentage`, `PaymentMethod`, `ProductId`, `StockBalance`, `Product` e `SaleItem` já possuem implementação inicial no `server-local` e devem ser tratados como tipos de domínio explícitos, não como primitivos soltos na regra de negócio.

Contratos já validados por testes:
- `Money`
  - rejeita `null` na criação
  - normaliza escala monetária para 2 casas com `HALF_UP`
  - permite soma, subtração, multiplicação por `Quantity` e `max`
  - permite inspeção por `isNegative` e `isZero`
  - expõe `value()` retornando `BigDecimal` já normalizado com escala monetária do domínio
- `Quantity`
  - cria valor válido quando `value > 0`
  - rejeita `0`
  - rejeita valor negativo
- `StockBalance`
  - cria saldo válido quando `value >= 0`
  - aceita `0` como estado válido de saldo
  - rejeita valor negativo
  - permite soma com `Quantity`
  - permite subtração com `Quantity` quando o resultado permanece `>= 0`
  - rejeita subtração que produziria saldo negativo
  - permite comparação entre instâncias válidas
  - rejeita comparação com `null`
  - permite inspeção por `isZero()`
- `Percentage`
  - aceita `0.00`
  - aceita `100.00`
  - aplica normalização com `HALF_UP`
  - rejeita `null`
  - rejeita valor negativo
  - rejeita valor acima de `100`
  - permite comparação entre instâncias válidas
- `PaymentMethod`
  - expõe conjunto fechado de métodos suportados: `CASH`, `DEBIT`, `CREDIT`, `PIX`
  - permite resolução nominal via `valueOf`
  - rejeita valor textual desconhecido
  - rejeita `null` conforme comportamento padrão de enum Java
- `ProductId`
  - cria valor válido quando `value > 0`
  - rejeita `null`
  - rejeita `0`
  - rejeita valor negativo
  - expõe `value()` retornando o identificador tipado do produto
  - implementa igualdade semântica e `hashCode()` com base no valor
- `Product`
  - cria entidade válida quando `productId`, `name` e `unitPrice` respeitam as invariantes
  - rejeita `productId` nulo
  - rejeita `name` nulo
  - rejeita `name` em branco após `trim()`
  - normaliza `name` com `trim()` antes de persistir o estado interno
  - rejeita `unitPrice` nulo
  - rejeita `unitPrice` zero
  - rejeita `unitPrice` negativo
- `SaleItem`
  - cria entidade válida quando `productId`, `productName`, `unitPrice` e `quantity` respeitam as invariantes
  - rejeita `productId` nulo
  - rejeita `productName` nulo
  - rejeita `productName` em branco após `trim()`
  - normaliza `productName` com `trim()` antes de persistir o estado interno
  - rejeita `unitPrice` nulo
  - rejeita `unitPrice` zero
  - rejeita `unitPrice` negativo
  - rejeita `quantity` nula
  - calcula `lineTotal()` internamente a partir de `unitPrice * quantity`

### PaymentMethod
- enum: `CASH`, `DEBIT`, `CREDIT`, `PIX`

---

## Estados do Carrinho e Transições

Estados:
- `EDITABLE`
- `CHECKOUT_STARTED`

Transições válidas:
1. `EDITABLE -> CHECKOUT_STARTED`
Pré-condição: carrinho válido para iniciar venda.

Regra de proteção:
- em `EDITABLE`, o carrinho aceita mutações e pode ser persistido/recuperado
- em `CHECKOUT_STARTED`, o carrinho torna-se imutável
- qualquer mutação em estado bloqueado gera erro de domínio

---

## Estados da Venda e Transições

Estados:
- `OPEN`
- `PAID`

Transições válidas:
1. `OPEN -> PAID`
Pré-condição: pagamento válido conforme método.

Regra de proteção:
- em `OPEN`, a venda aceita cálculo de desconto, taxa e registro de pagamento
- em `PAID`, a venda torna-se imutável
- qualquer tentativa de mutação em `PAID` gera erro de domínio

---

## Regras Críticas do V1

1. `Stock` nunca pode ficar negativo.
2. `Cart` soma quantidade quando o mesmo produto é adicionado novamente.
3. `Cart` editável pode ser persistido e recuperado sem perder consistência.
4. `Cart` em `CHECKOUT_STARTED` é imutável.
5. `Sale` não pode finalizar sem pagamento válido.
6. `finalTotal` reflete desconto e taxa.
7. `Sale` em estado `PAID` é imutável.
8. todos os valores monetários usam `Money`.

---

## Diferenças ao sistema Python

1. Paradigma e estrutura
- Python original: funções procedurais com regras distribuídas.
- PDVjava: modelagem OO com Aggregate Roots (`Cart` e `Sale`) e encapsulamento de invariantes.

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

- `carrinho` -> `Cart`
- `item_carrinho` -> `SaleItem`
- `produto_id` -> `ProductId`
- `qtd` -> `Quantity`
- `preco` -> `Money`
- `metodo_pagamento` -> `PaymentMethod`
- `valor_pago` -> `paidAmount: Money`
- `fechamento_venda_a_partir_do_carrinho` -> `Cart.startSale()` seguido da criação de `Sale`
- `resultado_venda` -> saída de caso de uso (application layer)

---

Este modelo pode evoluir por ADR e atualização incremental do roadmap.
