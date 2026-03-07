# ADR 0003 — Política de Dinheiro (Money)

## Status
Aceito

## Contexto

O sistema PDVjava realiza operações financeiras críticas (preço de produto, total de venda, desconto, taxa, pagamento e troco).

Uso inadequado de tipos numéricos pode gerar inconsistências silenciosas e perda de precisão.

É necessário formalizar uma política monetária única para todo o domínio.

---

## Decisão

1. Tipo base

Todos os valores monetários serão representados por um Value Object `Money` baseado em `BigDecimal`.

`float` e `double` são proibidos no domínio.

---

2. Escala

Todos os valores monetários serão normalizados para 2 casas decimais.

Escala fixa: 2.

---

3. Política de arredondamento

Será utilizado:

`RoundingMode.HALF_UP`

Justificativa:

- Comportamento intuitivo para varejo.
- Compatível com expectativa comum de arredondamento comercial.
- Simples e previsível.

---

4. Centralização do arredondamento

O arredondamento será aplicado:

- Na criação do objeto Money
- Após cada operação aritmética

Nenhuma classe externa deve aplicar `setScale`.

---

5. Igualdade monetária

Comparação de igualdade será baseada em `compareTo == 0`, ignorando diferença de escala.

---

6. Valores negativos

O Value Object `Money` permitirá valores negativos.

As regras de domínio serão responsáveis por restringir seu uso conforme necessário.

---

## Consequências

- Consistência monetária em todo o sistema.
- Redução de bugs financeiros.
- Centralização da lógica monetária.
- Maior confiabilidade no domínio.

---

## Impacto Futuro

Caso o sistema evolua para múltiplas moedas, o Value Object poderá ser expandido para incluir código de moeda (ISO 4217).