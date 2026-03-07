# Arquitetura do PDVjava

## Visão Estratégica

O sistema adota arquitetura híbrida Local-First:

- Operação primária offline
- Sincronização periódica com servidor central
- Baixa dependência de infraestrutura externa

---

## Estrutura Física

### 1. Server Central (Cloud)

Responsável por:

- Gerenciamento de licenças
- Controle de versões
- Distribuição de atualizações

Tecnologia:
- Java
- Spring Boot
- PostgreSQL

---

### 2. Server Local (Cliente)

Responsável por:

- Regras de negócio
- API REST local
- Persistência transacional
- Controle de estoque e caixa

Este é o núcleo do sistema.

---

### 3. PDV Desktop

Responsável por:

- Interface do operador
- Consumo da API local

Não contém regra de negócio crítica.

---

## Arquitetura Interna (Server Local)

Organização em camadas inspirada em Clean Architecture:

- Domain → regras puras
- Application → orquestração de casos de uso
- Infrastructure → banco e integrações
- Presentation → controllers REST

### Regra fundamental:

Dependências sempre apontam para dentro.

Domain não depende de nada.
Application depende apenas de Domain.
Infrastructure depende de Application.
Presentation depende de Application.

---

## Estratégia de Evolução

Fase 1 → Domain puro + testes
Fase 2 → Application layer
Fase 3 → Persistência
Fase 4 → API local
Fase 5 → Integrações

Complexidade incremental é obrigatória.

Documento de execução detalhada por fase:
- `docs/DOMAIN_ROADMAP.md`
