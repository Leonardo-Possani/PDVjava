# DOMAIN ROADMAP — Execução Profissional

Roadmap oficial para evoluir o `server-local` de documentação para software executável.
Este roadmap está alinhado com `docs/ARCHITECTURE.md`.

## Escopo do MVP de Domínio (V1)

IN:
- Venda (`Sale`, `SaleItem`)
- Estoque (`Product`, `Stock`)
- Pagamento (`PaymentMethod`)
- Value Objects (`Money`, `Quantity`, `Percentage`)

OUT:
- `CashRegister` (adiado para versão futura)
- Cancelamento/estorno (adiado para versão futura)
- Sincronização distribuída

---

## Fase 1 — Domain Puro + Testes

Objetivo:
- construir o núcleo de regras de negócio sem framework

Entregáveis:
- estrutura de pacotes do domínio
- `Money`, `Quantity`, `Percentage`, `PaymentMethod`
- `Sale`, `SaleItem`, `Product`, `Stock`
- regras de transição de estado da venda
- testes unitários cobrindo invariantes críticas

Anti-objetivos:
- não criar controllers
- não criar repositório real
- não adicionar anotações de framework no domínio

Gate de qualidade:
- todos os testes passando
- nenhum `float`/`double` no domínio
- APIs públicas sem permitir estado inválido

---

## Fase 2 — Application Layer

Objetivo:
- orquestrar os fluxos sem deslocar regra para fora do domínio

Entregáveis:
- `FinalizeSaleUseCase`
- `RegisterPaymentUseCase`
- contratos de entrada e saída

Anti-objetivos:
- não mover validações críticas para use cases
- não acoplar use case a tecnologia HTTP

Gate de qualidade:
- use case apenas coordena
- regra crítica segue no domínio
- testes de aplicação validam orquestração

---

## Fase 3 — Persistência

Objetivo:
- persistir estado mantendo separação arquitetural

Entregáveis:
- interfaces de repositório na camada correta
- implementação inicial em infraestrutura
- estratégia transacional explícita

Anti-objetivos:
- não vazar SQL/JPA para domínio
- não acoplar contrato de aplicação ao banco

Gate de qualidade:
- persistência integrada com casos de uso
- domínio permanece puro e testável isoladamente

---

## Fase 4 — API Local (Presentation)

Objetivo:
- expor operações essenciais para o desktop

Entregáveis:
- endpoints de venda, pagamento e consulta de estoque
- mapeamento de erros de domínio para HTTP

Anti-objetivos:
- não validar regra crítica no controller
- não duplicar regra de domínio em DTO mapper

Gate de qualidade:
- fluxo ponta a ponta local funcionando
- erro funcional devolvendo resposta consistente

---

## Fase 5 — Integrações

Objetivo:
- integrar com `server-central` de forma incremental e segura

Entregáveis:
- mecanismo inicial de validação periódica de licença
- estratégia de versionamento/atualização controlada
- contratos de integração documentados

Anti-objetivos:
- não bloquear operação local por indisponibilidade temporária
- não acoplar regra de venda ao servidor central

Gate de qualidade:
- operação local preservada offline
- falha de integração não corrompe domínio local

---

## Regras de Execução do Roadmap

1. Não avançar de fase com testes quebrados.
2. Cada fase deve gerar commits pequenos e rastreáveis.
3. Toda decisão arquitetural nova deve virar ADR.
4. No fim de cada fase, atualizar:
- `docs/CODEX.md`
- `docs/DOMAIN_MODEL_V1.md` (se houver impacto de domínio)
- `docs/ADR/` (se houver decisão arquitetural)
