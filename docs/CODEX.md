# CONTEXTO DO PROJETO — PDVjava

## 1. Objetivo do Produto

PDVjava é um sistema de ponto de venda local-first para pequenos estabelecimentos (até 3 caixas), com foco em:

- operação offline confiável
- consistência transacional
- arquitetura profissional e evolutiva
- viabilidade de comercialização por assinatura

---

## 2. Estado Atual (Março/2026)

- Estrutura multi-módulo Maven criada:
  - `server-local`
  - `server-central`
  - `pdv-desktop`
- Arquitetura e decisões principais documentadas (`docs/ARCHITECTURE.md` + ADRs 0001, 0002, 0003).
- Descoberta de domínio consolidada (`docs/DOMAIN_DISCOVERY.md`).
- Modelo inicial de domínio definido (`docs/DOMAIN_MODEL_V1.md`).
- Roadmap de execução alinhado em `docs/DOMAIN_ROADMAP.md`.
- Código de domínio ainda não implementado (fase de preparação arquitetural).

---

## 3. Decisões Técnicas Vigentes

1. Arquitetura em camadas inspirada em Clean Architecture.
Dependências sempre apontam para dentro: `Domain <- Application <- Infrastructure <- Presentation`.

2. Estratégia local-first.
Operação principal offline; sincronização e validação de licença ocorrem de forma periódica.

3. Política monetária única.
`Money` será Value Object com `BigDecimal`, escala 2 e `RoundingMode.HALF_UP`.

---

## 4. Escopo da Fase Atual (Fase 1)

Construir domínio puro no `server-local` com testes, sem dependência de framework:

- Value Objects: `Money`, `Quantity`, `Percentage`, `PaymentMethod`
- Entidades iniciais: `Sale`, `SaleItem`, `Product`, `Stock`
- Invariantes críticas de venda, pagamento e estoque
- Testes unitários de domínio como documentação executável
- Fora de escopo no V1: `CashRegister` e cancelamento/estorno

---

## 5. Próximos Passos Imediatos

1. Criar estrutura de pacotes do `server-local` por camada.
2. Implementar `Money`, `Quantity`, `Percentage` e `PaymentMethod`.
3. Implementar `Sale` com transições de estado explícitas.
4. Implementar `Product`, `SaleItem` e `Stock`.
5. Cobrir invariantes críticas com JUnit.

---

## CI Policy (V1)

Gates bloqueantes no Pull Request:
- `mvn -B -ntp verify`
- `mvn -B -ntp spotless:check`

Opcional por enquanto:
- `mvn -B -ntp spotbugs:check`

Evolução planejada:
1. V1.1: adicionar `spotbugs:check`.
2. V2: adicionar `jacoco` e `dependency-check`.
3. V3: adicionar `sonarcloud`.

Definition of Done da fatia:
- CI verde no Pull Request.

---

## 6. Ritual de Ciclo Profissional (Obrigatório)

Cada entrega deve seguir este fluxo:

1. Escolher uma fatia pequena do roadmap.
2. Definir contrato da fatia (regra, API mínima, erros esperados).
3. Planejar testes antes de implementar.
4. Implementar o mínimo necessário.
5. Revisar tecnicamente com foco em arquitetura e invariantes.
6. Aplicar melhoria curta sem alterar comportamento.
7. Atualizar documentação do ciclo.
8. Fechar com commit padronizado.

---

## 7. Ritual Oficial de Sessão (Abertura -> Execução -> Encerramento)

### 7.1 Abertura de sessão

Objetivo: iniciar com contexto máximo e escopo mínimo.

Passos:
1. Ler `docs/CODEX.md` e fase atual de `docs/DOMAIN_ROADMAP.md`.
2. Escolher uma única fatia pequena do roadmap.
3. Definir:
- objetivo da fatia
- fora de escopo
- invariantes
- critério de pronto

Saída esperada:
- mini-brief da sessão com foco único.

### 7.2 Planejamento técnico

Objetivo: reduzir ambiguidade antes de codar.

Passos:
1. Definir contrato da fatia (API mínima, regras e erros esperados).
2. Planejar testes antes da implementação.
3. Garantir no mínimo:
- 1 cenário válido
- 1 cenário inválido crítico
- 1 cenário de borda

Saída esperada:
- lista de testes nomeados e contrato definido.

### 7.3 Execução

Objetivo: avançar com qualidade e baixa dispersão.

Passos:
1. Implementar o mínimo para os testes passarem.
2. Trabalhar em blocos curtos (25-40 minutos).
3. Ao fim de cada bloco:
- rodar testes
- revisar design rapidamente
- evitar refatoração grande

Regra:
- não expandir escopo durante o ciclo.

### 7.4 Revisão orientada

Objetivo: elevar qualidade sem perder ritmo.

Checklist:
1. Camada correta?
2. Invariantes protegidas?
3. Nomes e legibilidade adequados?
4. Risco de regressão aceitável?
5. Aderência ao `docs/CODING_RULES.md`?

Saída esperada:
- lista curta e objetiva de ajustes.

### 7.5 Melhoria curta

Objetivo: refinar sem alterar comportamento.

Passos:
1. Aplicar ajustes pontuais.
2. Reexecutar testes.

Saída esperada:
- versão estável, legível e coerente com arquitetura.

### 7.6 Encerramento de ciclo

Objetivo: preservar aprendizado e continuidade.

Passos:
1. Preencher o Template de Fechamento de Ciclo neste arquivo.
2. Registrar concluído, pendências e próximo ciclo recomendado.
3. Atualizar `docs/DOMAIN_MODEL_V1.md` se contrato de domínio mudou.
4. Criar/atualizar ADR se decisão arquitetural nova foi tomada.

### 7.7 Encerramento de sessão

Objetivo: preparar retomada eficiente.

Passos:
1. Preencher o Template de Fechamento de Sessão.
2. Deixar o próximo passo explícito e pronto para execução.

---

## 8. Comportamento Mentor-Aprendiz (Regra de Trabalho)

Você (autor):
1. Escreve o código.
2. Expõe dúvidas cedo.
3. Não pula a etapa de testes.

Mentor (assistente):
1. Quebra trabalho em fatias pequenas.
2. Ensina com foco didático e prático.
3. Revisa contrato, testes e código.
4. Não implementa nada sem autorização explícita do autor.
5. Quando autorizado, implementa apenas apoio pontual, mantendo o autor como implementador principal para aprendizado.

Ambos:
1. Uma fatia por vez.
2. Documentação curta, viva e acionável.
3. Decisões explícitas para evitar retrabalho.

---

## 9. Métricas de Eficiência de Aprendizado

1. Percentual de ciclos concluídos sem expansão de escopo.
2. Percentual de regras P0 cobertas com teste válido e inválido.
3. Tempo por ciclo (meta: 60 a 120 minutos por fatia).
4. Quantidade de dúvidas conceituais resolvidas por sessão.

---

## 10. Riscos e Atenções

- Risco de anemização do domínio (regras escapando para service).
- Risco de misturar regra de negócio com detalhes de framework cedo demais.
- Risco de divergência entre documentação e implementação.
- Risco de falta de política de concorrência para estoque no cenário multiusuário.

---

## 11. Regras de Continuidade entre Sessões

- Toda decisão nova deve atualizar:
  - `docs/ADR/` (quando arquitetural)
  - `docs/DOMAIN_MODEL_V1.md` (quando de domínio)
  - este arquivo `docs/CODEX.md` (estado e próximos passos)
- Cada sessão deve terminar com:
  - o que foi concluído
  - o que ficou pendente
  - qual é o próximo passo recomendado

---

## 12. Decisões de Escopo Fechadas

1. `CashRegister` está fora do V1.
2. Cancelamento/estorno está fora do V1.
3. Nome oficial da camada de interface: `Presentation`.

---

## 13. Template de Fechamento de Ciclo

Use este bloco ao final de cada ciclo:

- Fatia trabalhada:
- Contrato definido:
- Testes planejados:
- Implementação mínima concluída:
- Achados de revisão:
- Melhorias aplicadas:
- Documentação atualizada:
- Próximo ciclo recomendado:

---

## 14. Template de Fechamento de Sessão

Use este bloco ao final de cada sessão:

- Concluído:
- Pendências:
- Riscos ativos:
- Próximo passo recomendado:
- Instrução de retorno (prompt padrão):
  - "Leia `docs/CODEX.md`, `docs/DOMAIN_ROADMAP.md`, `docs/DOMAIN_MODEL_V1.md`, `docs/CODING_RULES.md` e `docs/DOMAIN_DISCOVERY.md` e inicie o próximo ciclo pela fatia definida no próximo passo recomendado."

---

## 15. Fechamento de Sessão — 2026-03-01

- Concluído:
  - alinhamento entre arquitetura e roadmap por fases
  - formalização do escopo V1 (IN/OUT), incluindo `CashRegister` e cancelamento fora do V1
  - atualização do `DOMAIN_DISCOVERY` com glossário Python -> Java e regras priorizadas (P0/P1/P2)
  - atualização do `DOMAIN_MODEL_V1` com contrato implementável (aggregate, entidades, VOs, transições e mapa de migração)
  - reforço do `CODING_RULES` com ciclo profissional, checklist de PR e padrões de testes/exceções
  - inclusão do Ritual Oficial de Sessão e métricas de aprendizado no contexto

- Pendências:
  - iniciar Fase 1 com implementação dos Value Objects no `server-local`
  - definir limites superiores de domínio para valores monetários/quantidade (regra P1)

- Riscos ativos:
  - risco de expandir escopo durante a primeira implementação prática
  - risco de inconsistência entre modelo e código se o ciclo de revisão for pulado

- Próximo passo recomendado:
  - abrir nova sessão com a fatia `Money` (contrato + testes planejados + implementação mínima)

---

## 16. Fechamento de Sessão — 2026-03-01 (ajustes finais de prontidão)

- Concluído:
  - definição explícita de `ProductId` e `SaleId` em `docs/DOMAIN_MODEL_V1.md`
  - decisão final de taxa como `Money` (`taxAmount`) em `docs/DOMAIN_DISCOVERY.md`
  - redução de ambiguidade para início da Fase 1 de implementação

- Pendências:
  - iniciar Ciclo 1 com implementação de `Money` orientada por testes
  - ajustar typo de `Presentation` no `README.md` para manter consistência documental

- Riscos ativos:
  - risco de desvio de escopo no primeiro ciclo de código
  - risco de pular revisão de invariantes durante implementação inicial

- Próximo passo recomendado:
  - abrir sessão e executar Ciclo 1 da fatia `Money` (contrato -> testes -> implementação mínima -> revisão -> documentação)

- Instrução de retorno (prompt padrão):
  - "Leia `docs/CODEX.md`, `docs/DOMAIN_ROADMAP.md`, `docs/DOMAIN_MODEL_V1.md`, `docs/CODING_RULES.md` e `docs/DOMAIN_DISCOVERY.md` e inicie o próximo ciclo pela fatia definida no próximo passo recomendado."
