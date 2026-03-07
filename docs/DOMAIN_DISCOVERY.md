# DOMAIN DISCOVERY

## Status

Este documento registra a extração de regras do sistema Python original.
Ele é a base de descoberta, não o contrato final de implementação.
Contrato alvo Java: `docs/DOMAIN_MODEL_V1.md`.

---

## Escopo V1 (MVP de Domínio)

IN:
- Venda
- Item de venda
- Produto
- Estoque
- Pagamento (dinheiro, débito, crédito)
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
- `carrinho` -> `Sale` + `SaleItem` (no fluxo Java)
- `venda_fechada` -> `Sale` em estado `PAID`
- `estoque_atualizado` -> `Stock` após baixa
- `resultado_venda` -> output de caso de uso (`FinalizeSaleResult`)

---

## Entidades de Descoberta

1. Produto
- item vendável com identidade, nome, preço e quantidade disponível

2. Estoque
- conjunto de produtos e suas quantidades correntes

3. Venda
- transação que agrega itens, totais e quitação

4. Item de venda
- produto, quantidade e preço snapshot da venda

5. Método de pagamento
- enum de formas aceitas: `CREDIT`, `DEBIT`, `CASH`

---

## Regras Implementáveis (priorizadas)

Formato:
- `Regra`
- `Entrada`
- `Validação`
- `Erro esperado`

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
- Regra: em débito/crédito, valor pago deve ser exatamente igual ao total final.
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
- Regra: unicidade de `ProductId` no estoque.
- Entrada: coleção de produtos de estoque.
- Validação: IDs não repetidos.
- Erro esperado: `DomainValidationException("duplicated product id in stock")`.

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
2. Venda não avança em ordem inválida de estados.
3. Venda não pode ser finalizada sem pagamento válido.
4. Quantidade de item é sempre maior que zero.
5. Resultado de fechamento mantém consistência entre venda e estoque.

---

## Gap entre legado e alvo Java

1. Legado usa `float`; alvo Java usa `Money` com `BigDecimal`.
2. Legado usa fluxo procedural; alvo Java usa Aggregate Root e Value Objects.
3. Legado tem validações implícitas; alvo Java exige contratos explícitos de erro.
