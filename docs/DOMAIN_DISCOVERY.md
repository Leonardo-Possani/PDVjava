# DOMAIN DISCOVERY

## Status

Este documento registra a extração de regras do sistema Python original.
Ele é a base de descoberta, não o contrato final de implementação.
Contrato alvo Java: `docs/DOMAIN_MODEL_V1.md`.

---

## Escopo V1 (MVP de Domínio)

IN:
- Carrinho
- Venda
- Item de venda
- Produto
- Estoque
- Pagamento (dinheiro, débito, crédito, pix)
- Cálculo de total com desconto e taxa

OUT:
- `CashRegister`
- Cancelamento/estorno
- Multi-moeda
- Sincronização distribuída entre lojas

---

## Glossário Oficial (Python -> Java)

- `produto_id` -> `ProductId`
- `qtd` -> `Quantity`
- `preco` -> `Money`
- `metodo_pagamento` -> `PaymentMethod`
- `valor_pago` -> `Money` (paid amount)
- `carrinho` -> `Cart`
- `venda_fechada` -> `Sale` em estado `PAID`
- `estoque_atualizado` -> `Stock` após baixa
- `resultado_venda` -> output de caso de uso (`FinalizeSaleResult`)

---

## Entidades de Descoberta

1. Produto
- item vendável com identidade, nome, preço e quantidade disponível

2. Carrinho
- agregado editável que concentra os itens selecionados antes da venda
- pode ser persistido e recuperado para continuar edição
- calcula seu valor total corrente
- quando o mesmo produto é adicionado novamente, soma quantidade em vez de duplicar linha
- torna-se imutável quando o processo de venda é iniciado a partir de um carrinho válido

3. Estoque
- conjunto de produtos e suas quantidades correntes

4. Venda
- transação que agrega itens, totais e quitação
- nasce a partir de um carrinho válido já bloqueado para edição

5. Item de venda
- produto, quantidade e preço snapshot da venda

6. Método de pagamento
- enum de formas aceitas: `CREDIT`, `DEBIT`, `CASH`, `PIX`

---

## Regras Implementáveis (priorizadas)

Formato:
- `Regra`
- `Entrada`
- `Validação`
- `Erro esperado`

### P0 — Carrinho

Regra `CART-001`:
- Regra: carrinho editável pode existir e ser persistido mesmo sem itens.
- Entrada: estado inicial do carrinho.
- Validação: carrinho vazio é válido enquanto estiver em modo de edição.
- Erro esperado: não aplicável.

Regra `CART-002`:
- Regra: carrinho deve calcular o total corrente como soma de `item.price * item.quantity`.
- Entrada: itens válidos no carrinho.
- Validação: total calculado internamente pela entidade.
- Erro esperado: não aplicável.

Regra `CART-003`:
- Regra: ao adicionar produto já existente no carrinho, deve somar quantidade em vez de duplicar linha.
- Entrada: item com `ProductId` já existente no carrinho.
- Validação: manter uma única linha por `ProductId`.
- Erro esperado: não aplicável.

Regra `CART-004`:
- Regra: carrinho pode ser recuperado persistido e continuar editável.
- Entrada: estado persistido do carrinho.
- Validação: itens e total devem permanecer consistentes após recuperação.
- Erro esperado: não aplicável.

Regra `CART-005`:
- Regra: ao iniciar o processo de venda a partir de um carrinho válido, o carrinho torna-se imutável.
- Entrada: ação de iniciar venda.
- Validação: novas mutações no carrinho devem ser bloqueadas após o início do checkout.
- Erro esperado: `DomainValidationException("cart is not editable")`.

### P0 — Venda

Regra `SALE-001`:
- Regra: não fechar venda sem itens.
- Entrada: lista de itens.
- Validação: lista não pode ser vazia.
- Erro esperado: `DomainValidationException("sale must contain at least one item")`.

Regra `SALE-002`:
- Regra: não permitir produto duplicado nos itens da venda.
- Entrada: itens com `ProductId`.
- Validação: `ProductId` deve ser único por venda.
- Erro esperado: `DomainValidationException("duplicated product in sale items")`.

Regra `SALE-003`:
- Regra: total bruto deve ser soma de `item.price * item.quantity`.
- Entrada: itens válidos.
- Validação: total calculado internamente pela entidade.
- Erro esperado: não aplicável (regra de cálculo interno).

Regra `SALE-004`:
- Regra: desconto deve estar entre 0 e 100.
- Entrada: `Percentage`.
- Validação: `0 <= percentage <= 100`.
- Erro esperado: `DomainValidationException("discount percentage out of range")`.

Regra `SALE-005`:
- Regra: taxa não pode ser negativa.
- Entrada: `Money` (`taxAmount`).
- Validação: valor >= 0.
- Erro esperado: `DomainValidationException("tax cannot be negative")`.

Regra `SALE-006`:
- Regra: sequência obrigatória de estados da venda.
- Entrada: ações do fluxo (desconto, taxa, pagamento).
- Validação: transições somente permitidas no estado correto.
- Erro esperado: `DomainValidationException("invalid sale state transition")`.

Regra `SALE-007`:
- Regra: venda deve nascer a partir de um carrinho válido já bloqueado para edição.
- Entrada: carrinho em início de checkout.
- Validação: `Sale` não é o carrinho; ela representa a transação derivada de um `Cart` válido.
- Erro esperado: `DomainValidationException("sale must be created from a valid cart")`.

### P0 — Pagamento

Regra `PAY-001`:
- Regra: método de pagamento deve ser válido.
- Entrada: `PaymentMethod`.
- Validação: enum conhecido e não nulo.
- Erro esperado: `DomainValidationException("invalid payment method")`.

Regra `PAY-002`:
- Regra: em dinheiro, valor pago deve ser >= total final.
- Entrada: `totalFinal`, `paidAmount`.
- Validação: `paidAmount >= totalFinal`.
- Erro esperado: `DomainValidationException("insufficient cash payment")`.

Regra `PAY-003`:
- Regra: em débito/crédito/pix, valor pago deve ser exatamente igual ao total final.
- Entrada: `totalFinal`, `paidAmount`.
- Validação: `paidAmount.compareTo(totalFinal) == 0`.
- Erro esperado: `DomainValidationException("non-cash payment must match total amount")`.

### P0 — Estoque

Regra `STOCK-001`:
- Regra: produto referenciado na venda deve existir no estoque.
- Entrada: `SaleItem` + `Stock`.
- Validação: todo `ProductId` do item deve existir no estoque.
- Erro esperado: `DomainValidationException("product not found in stock")`.

Regra `STOCK-002`:
- Regra: não vender quantidade maior que disponível.
- Entrada: `SaleItem` + `Stock`.
- Validação: `item.quantity <= stock.quantity`.
- Erro esperado: `DomainValidationException("insufficient stock")`.

Regra `STOCK-003`:
- Regra: baixa de estoque não pode gerar negativo.
- Entrada: operação de baixa por item.
- Validação: resultado final de quantidade >= 0.
- Erro esperado: `DomainValidationException("stock cannot be negative")`.

### P1 — Robustez de Entrada

Regra `ROBUST-001`:
- Regra: validar limites superiores de preço/quantidade/taxa.
- Entrada: VOs monetários e quantitativos.
- Validação: limites máximos definidos por política de domínio.
- Erro esperado: `DomainValidationException("value exceeds allowed limit")`.

Regra `ROBUST-002`:
- Regra: o domínio `Stock` recebe entradas já normalizadas por `ProductId`.
- Entrada: `Map<ProductId, StockBalance>` materializado pela infraestrutura.
- Validação: a unicidade de `ProductId` é garantida na persistência e na borda de infraestrutura que monta o `Map`.
- Erro esperado: não aplicável dentro de `Stock`; inconsistências de duplicidade devem falhar antes da materialização do domínio.

### P2 — Pós-MVP

Regra `POST-001`:
- Regra: cancelamento com estorno e reposição de estoque.
- Status: fora do V1.

Regra `POST-002`:
- Regra: estratégia de concorrência de estoque em múltiplos operadores.
- Status: fora do V1.

---

## Invariantes Consolidadas do V1

1. Estoque nunca pode ficar negativo.
2. Carrinho soma quantidade quando o mesmo produto é adicionado novamente.
3. Carrinho editável pode ser persistido e recuperado sem perder consistência.
4. Carrinho iniciado para venda torna-se imutável.
5. Venda não avança em ordem inválida de estados.
6. Venda não pode ser finalizada sem pagamento válido.
7. Quantidade de item é sempre maior que zero.
8. Resultado de fechamento mantém consistência entre carrinho, venda e estoque.

---

## Gap entre legado e alvo Java

1. Legado usa `float`; alvo Java usa `Money` com `BigDecimal`.
2. Legado usa fluxo procedural; alvo Java usa Aggregate Roots (`Cart` e `Sale`) e Value Objects.
3. Legado tem validações implícitas; alvo Java exige contratos explícitos de erro.
