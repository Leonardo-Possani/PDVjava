# PDVjava

PDVjava é um sistema de Ponto de Venda (PDV) local-first, projetado para pequenos estabelecimentos com até 3 caixas, com foco em estabilidade, operação offline e arquitetura profissional.

Este projeto é tratado como produto real, com decisões técnicas, arquitetura estruturada e visão de comercialização.

---

## 🎯 Objetivo

Construir um sistema:

- Operacional offline
- Confiável e transacional
- Comercializável via assinatura mensal
- Arquiteturalmente sólido
- Evolutivo e sustentável

O projeto também serve como laboratório de engenharia de software aplicada.

---

## 🧱 Arquitetura Geral

Estrutura física dividida em três aplicações:

- `server-central` → Controle de licenças, versões e atualizações
- `server-local` → Motor de regras de negócio e API local
- `pdv-desktop` → Interface do operador (JavaFX)

Arquitetura interna do `server-local`:

- Domain
- Application
- Infrastructure
- Presentaion

Dependências sempre apontam para dentro.

---

## 🧠 Princípios Fundamentais

- Transações atômicas obrigatórias
- Nenhuma venda pode "quase acontecer"
- Domínio isolado de frameworks
- Simplicidade operacional acima de sofisticação técnica
- Consistência > estética

---

## 📚 Documentação

- `docs/DOMAIN_DISCOVERY.md`
- `docs/DOMAIN_MODEL_V1.md`
- `docs/ARCHITECTURE.md`
- `docs/ADR/`

Testes são tratados como documentação executável.

---

## 🚧 Status

Fase atual: Estruturação do domínio puro (server-local)
## Commit Pattern - PDVjava

This project follows a strict and structured commit convention to ensure architectural clarity, traceability, and professional-grade evolution control.

All commits must be written in English by default.

### Commit Format

```text
<type>(<layer>/<scope>): <imperative short message>

<context>
- What was changed
- Why it was changed
- Rule or invariant involved (if applicable)

Impact:
- Domain impact:
- Architectural impact:
- Breaking change: yes/no
```

### Allowed Types

- `feat` -> Introduces new business behavior or rule
- `fix` -> Fixes incorrect behavior
- `refactor` -> Structural change without modifying behavior
- `test` -> Adds or improves automated tests
- `docs` -> Documentation updates (README, ADR, diagrams)
- `chore` -> Build configuration, dependencies, tooling
- `freeze` -> Stable version milestone

### Allowed Layers

These represent the architectural layer affected by the change.

- `domain`
- `application`
- `infrastructure`
- `architecture`
- `build`
- `docs`

### Allowed Scopes (Domain Context)

Use when applicable to clarify the affected domain concept.

- `venda`
- `estoque`
- `produto`
- `cliente`
- `pagamento`
- `checkout`
- `entities`
- `usecase`
- `exceptions`
- `integration`
- `repository`

### Header Examples

```text
feat(domain/venda): add cancelation rule with stock reversal
refactor(application/usecase): extract finalize sale use case
test(domain/estoque): add unit tests for stock reduction rule
```

### Example of a Full Commit

```text
feat(domain/venda): implement cancelation with stock reversal

Adds cancelation behavior to the Sale entity.
Implements business rule to restore stock from sale items.
Prevents cancelation of finalized sales.

Impact:
- Domain impact: introduces new consistency rule between sale and stock
- Architectural impact: none
- Breaking change: no
```

### Commit Rules

- One commit = one architectural intention
- Commit messages must be written in English
- Avoid generic messages such as "adjustments" or "improvements"
- Every behavioral change must include or update tests
- Large refactors should be split into smaller logical commits

### Purpose

This pattern enforces:

- Clear architectural traceability
- Explicit domain evolution
- Clean separation of layers
- Professional development discipline
- Historical readability for future maintainers

This convention is mandatory for all contributions to the project.
