# ADR 0001 — Arquitetura em Camadas

## Status
Aceito

## Contexto

O sistema precisa proteger regras de negócio contra dependências externas voláteis.

## Decisão

Adotar arquitetura em camadas inspirada em Clean Architecture:

- Domain
- Application
- Infrastructure
- Presentation

Dependências apontam para dentro.

## Consequências

- Domínio isolado e testável
- Facilidade de troca de banco ou framework
- Complexidade inicial maior
- Manutenção mais segura no longo prazo