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
- base `Long`
- criação via factory `ProductId.of(Long value)`
- expõe `value()` para leitura do identificador
- igualdade semântica baseada no valor do identificador
- mensagens de erro atuais:
  - `DomainValidationException("product id cannot be null")`
  - `DomainValidationException("product id must be greater than zero")`

### SaleId
- identificador forte de venda (semântica de domínio)
- não pode ser nulo
- valor deve ser positivo

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

`Money`, `Quantity`, `Percentage`, `PaymentMethod`, `ProductId` e `Product` já possuem implementação inicial no `server-local` e devem ser tratados como tipos de domínio explícitos, não como primitivos soltos na regra de negócio.

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
