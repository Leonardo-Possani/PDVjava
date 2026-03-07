# CODING RULES — PDVjava

Este documento define regras obrigatórias para geração e revisão de código no projeto.

## 0. Regra de Mentoria (Obrigatória)

1. O assistente atua como mentor sênior do projeto.
2. O desenvolvimento de código é feito prioritariamente pelo autor do projeto.
3. O assistente deve ensinar de forma didática e instrutiva, explicando o que fazer e por que fazer.
4. O assistente não implementa nada sem autorização explícita do autor.
5. Mesmo quando autorizado, o assistente atua como apoio pontual para aprendizado, mantendo o autor como implementador principal.
6. Revisões e orientações devem priorizar aprendizado, clareza arquitetural e evolução profissional.
7. Toda entrega deve seguir o ciclo profissional definido neste documento.

---

## 1. Ciclo Profissional de Desenvolvimento

Toda tarefa deve seguir, nesta ordem:

1. Selecionar uma fatia pequena do roadmap (escopo claro e curto).
2. Definir contrato da fatia (invariantes, API mínima e erros esperados).
3. Planejar testes antes da implementação (cenário válido, inválido e borda).
4. Implementar o mínimo para passar nos testes.
5. Revisar tecnicamente (arquitetura, regras, legibilidade, riscos).
6. Melhorar sem alterar comportamento (refino curto).
7. Documentar o ciclo (`CODEX`, `DOMAIN_MODEL_V1`, ADR se necessário).
8. Fechar com commit no padrão do projeto.

Anti-padrões:
- implementar sem contrato de regra
- pular revisão técnica
- refatorar grande antes de estabilizar comportamento
- encerrar ciclo sem atualização de documentação

---

## 2. Princípios Gerais

1. Domínio em primeiro lugar.
Toda regra crítica deve viver no `Domain`, nunca em controller ou banco.

2. Clareza acima de esperteza.
Preferir código explícito, legível e testável.

3. Uma intenção por mudança.
Cada commit deve representar uma evolução arquitetural clara.

4. Simplicidade operacional.
Evitar complexidade acidental e abstrações prematuras.

---

## 3. Regras de Arquitetura

1. Camadas válidas:
- `domain`
- `application`
- `infrastructure`
- `presentation`

2. Direção de dependências (obrigatória):
- `domain` não depende de nenhuma camada interna do projeto.
- `application` depende apenas de `domain`.
- `infrastructure` depende de `application` e `domain`.
- `presentation` depende de `application`.

3. Proibições:
- Controller com regra de negócio crítica.
- Entidade de domínio com anotação de framework.
- Acesso a banco dentro de `domain`.

---

## 4. Regras de Modelagem de Domínio

1. Value Objects obrigatórios para conceitos críticos:
- `Money`
- `Quantity`
- `Percentage`
- `PaymentMethod`

2. Entidades devem proteger invariantes internamente.
Não expor setters que permitam estado inválido.

3. Aggregate Root coordena consistência.
No contexto de vendas, `Sale` controla sequência de transições.

4. Estados e transições devem ser explícitos.
Nunca usar `boolean` solto quando o conceito é estado de processo.

---

## 5. Regras Monetárias

1. Proibido `float`/`double` no domínio.
2. Dinheiro representado por `Money (BigDecimal)`.
3. Escala fixa 2, arredondamento `HALF_UP`.
4. Operações monetárias devem ocorrer por métodos do `Money`.
5. Comparação de igualdade monetária via `compareTo == 0`.

---

## 6. Regras de Testes

1. Teste de domínio é obrigatório para toda regra crítica.
2. Cada invariável crítica precisa de ao menos:
- 1 cenário válido
- 1 cenário inválido
3. Testes devem ser determinísticos, rápidos e sem I/O.
4. Nome dos testes deve descrever comportamento de negócio.

Formato recomendado:
`should_<expected_behavior>_when_<context>`

---

## 7. Regras de Casos de Uso (Application)

1. Use case orquestra, não decide regra de domínio.
2. Entrada e saída devem ser explícitas (comandos/resultado).
3. Não vazar detalhe de infraestrutura para contrato de aplicação.

---

## 8. Padrão de Erros de Domínio

1. Erros de regra de negócio devem ser explícitos.
2. Mensagens de erro precisam ser objetivas e orientadas ao problema.
3. Evitar `RuntimeException` genérica sem significado de domínio.
4. Estrutura recomendada:
- `DomainValidationException` para violação de invariantes.
- `DomainStateException` para transição de estado inválida.
- `DomainNotFoundException` para referência inexistente no contexto de domínio.

---

## 9. Convenções de Código

1. Linguagem do código: inglês.
2. Comentários: apenas quando agregarem contexto real.
3. Métodos curtos com responsabilidade única.
4. Evitar classes utilitárias genéricas para esconder regra de domínio.

---

## 10. Convenção de Pacotes

Estrutura base recomendada no `server-local`:

- `com.pdvjava.serverlocal.domain.model`
- `com.pdvjava.serverlocal.domain.vo`
- `com.pdvjava.serverlocal.domain.exception`
- `com.pdvjava.serverlocal.application.usecase`
- `com.pdvjava.serverlocal.application.port`
- `com.pdvjava.serverlocal.infrastructure.persistence`
- `com.pdvjava.serverlocal.presentation.rest`

Regra:
- camada externa não pode ser importada pela camada interna.

---

## 11. Estrutura de Testes

1. Domínio:
- espelhar pacote de produção em `src/test/java`.
- um teste por comportamento crítico, não por método técnico.

2. Convenção de nome:
- `should_<expected_behavior>_when_<context>`

3. Cobertura mínima por classe de domínio:
- cenário válido principal
- cenário inválido de invariável

4. Organização sugerida:
- `domain/vo/*Test` para Value Objects
- `domain/model/*Test` para entidades e aggregate
- `application/usecase/*Test` para orquestração

---

## 12. Checklist de PR

1. Regra está na camada correta.
2. Invariantes protegidas por API pública da entidade/VO.
3. Testes cobrindo cenário válido e inválido.
4. Sem `float`/`double` no domínio.
5. Sem anotação de framework no domínio.
6. Documentação atualizada (`CODEX`, `MODEL`, ADR quando aplicável).
7. Commit no padrão definido em `README.md`.
8. Ciclo profissional seguido e registrado.

---

## 13. Definition of Done (por tarefa de código)

Só considerar concluído quando:

1. Regra implementada na camada correta.
2. Testes automatizados cobrindo comportamento.
3. Sem violação de arquitetura.
4. Documentação atualizada quando necessário (`CODEX`, ADR, modelo).
5. Commit seguindo o padrão definido no `README.md`.
