# CONTEXTO DO PROJETO — PDVjava

## 1. Objetivo do Produto

PDVjava é um sistema de ponto de venda local-first para pequenos estabelecimentos (até 3 caixas), com foco em:

- operação offline confiável
- consistência transacional
- arquitetura profissional e evolutiva
- viabilidade de comercialização por assinatura

---

## 2. Estado Atual (Abril/2026)

- Estrutura multi-módulo Maven criada:
  - `server-local`
  - `server-central`
  - `pdv-desktop`
- Arquitetura e decisões principais documentadas (`docs/ARCHITECTURE.md` + ADRs 0001, 0002, 0003).
- Descoberta de domínio consolidada (`docs/DOMAIN_DISCOVERY.md`).
- Modelo inicial de domínio definido (`docs/DOMAIN_MODEL_V1.md`).
- Roadmap de execução alinhado em `docs/DOMAIN_ROADMAP.md`.
- Implementação do domínio iniciada no `server-local`, com `Value Objects`, `Product` e testes de domínio.
- Implementação do domínio expandida no `server-local` com `SaleItem` e testes de domínio da entidade.
- Micro-fatia de domínio concluída com `StockBalance` e testes dedicados para saldo de estoque `>= 0`.
- Fatia de domínio `Stock` concluída no `server-local`, com entrada normalizada por `Map<ProductId, StockBalance>`, operação de baixa imutável e testes cobrindo presença, consulta de saldo e proteção contra estoque inexistente ou negativo.
- Decisão de modelagem consolidada: `Cart` passa a existir explicitamente no V1 antes de `Sale`, como agregado persistível e recuperável em estado editável.

---

## 3. Decisões Técnicas Vigentes

1. Arquitetura em camadas inspirada em Clean Architecture.
Dependências sempre apontam para dentro: `Domain <- Application <- Infrastructure <- Presentation`.

2. Estratégia local-first.
Operação principal offline; sincronização e validação de licença ocorrem de forma periódica.

3. Política monetária única.
`Money` será Value Object com `BigDecimal`, escala 2 e `RoundingMode.HALF_UP`.

---

## 3A. Referência Técnica Operacional (Estrutura Adotada)

Esta seção centraliza o contexto técnico-operacional no mesmo arquivo durante a fase atual.
Quando o projeto escalar, cada bloco poderá ser extraído para documentos dedicados.

### 3A.1 Visão geral da arquitetura

- Alvo arquitetural: modelo local-first com 3 aplicações (`server-local`, `server-central`, `pdv-desktop`).
- Alvo interno do `server-local`: `Domain <- Application <- Infrastructure <- Presentation`.
- Estado atual: estrutura de módulos e documentação arquitetural consolidadas; implementação funcional iniciada pelos Value Objects do domínio no `server-local`, pelas entidades `Product`, `SaleItem` e `Stock`, e pela micro-fatia `StockBalance` como base do saldo de estoque. A próxima fatia de domínio passa a ser `Cart`, antecedendo `Sale`.
- Referências: `docs/ARCHITECTURE.md` e ADRs em `docs/ADR/`.

### 3A.2 Stack tecnológico completo

- Linguagem: Java 21.
- Build: Maven multi-módulo (`pom` agregador na raiz).
- Módulos: `server-local`, `server-central`, `pdv-desktop`.
- Testes: JUnit 5 (`junit-jupiter`).
- Runner de testes (`server-local`): Maven Surefire `3.2.5`.
- Qualidade de código: Spotless Maven Plugin `2.43.0` com Google Java Format `1.22.0`.
- Comandos padrão de qualidade (V1):
  - `mvn -B -ntp verify`
  - `mvn -B -ntp spotless:check`
- Tecnologias planejadas (ainda não implementadas no código): Spring Boot e PostgreSQL no `server-central`.

### 3A.3 Variáveis de ambiente

- Estado atual do repositório: nenhuma variável de ambiente obrigatória identificada.
- Não há `.env`, `application.yml`, `application.yaml` ou `application.properties` versionados até o momento.
- Regra de evolução: toda nova variável deve ser registrada neste arquivo antes de uso em produção.

Template:
- `NOME_VARIAVEL`: propósito, valor default, obrigatório (`sim`/`não`), módulo(s) afetado(s), exemplo de uso.

### 3A.4 Estrutura do diretório de conteúdo

- Estrutura atual da raiz:
  - `docs/`: documentação viva do projeto e ADRs.
  - `server-local/`: núcleo de domínio e evolução por fases.
  - `server-central/`: módulo reservado para integração central.
  - `pdv-desktop/`: módulo cliente desktop.
  - `pom.xml`: agregador Maven da solução.
  - `README.md`: visão geral e convenção de commits.
- Observação: diretórios `target/` existem nos módulos por builds locais e não fazem parte do código-fonte.

### 3A.5 Serviços, jobs e models por app

- `server-local`
  - Serviços/API: não implementados ainda.
  - Jobs: não implementados.
  - Models implementados: `Money`, `Quantity`, `Percentage`, `PaymentMethod`, `ProductId`, `SaleId`, `SaleStatus`, `StockBalance`, `CartId`, `CartStatus`, `Product`, `SaleItem`, `Stock`, `Cart` e `DomainValidationException`.
  - Cobertura atual de testes de domínio: contratos dos Value Objects implementados, incluindo `StockBalance`, `SaleId`, `SaleStatus`, `CartId` e `CartStatus`, e das entidades `Product`, `SaleItem`, `Stock` e do ciclo 1 de `Cart`.
  - Models planejados V1 ainda pendentes: evolução do `Cart` (ciclos 2 e 3) e `Sale`.
- `server-central`
  - Serviços/API: não implementados ainda.
  - Jobs: não implementados.
  - Models: não implementados ainda.
- `pdv-desktop`
  - UI e integração com API local: não implementadas ainda.
  - Jobs locais: não implementados.
  - Models locais: não implementados ainda.

### 3A.5A Convenção de pacotes vigente no código

- O `server-local` adota atualmente pacotes raiz em `com.pdvjava.*`.
- Domínio implementado até aqui:
  - `com.pdvjava.domain.vo`
  - `com.pdvjava.domain.exception`
  - `com.pdvjava.domain.model`
- Regra operacional:
  - a documentação deve acompanhar a estrutura vigente do código
  - não manter árvores vazias paralelas como `com.pdvjava.serverlocal.*`

### 3A.6 Common hurdles (com soluções documentadas)

- Misturar regra crítica em camada errada.
  - Solução: validar direção de dependências e manter regra no `domain`.
- Uso de `float`/`double` em regra monetária.
  - Solução: usar exclusivamente `Money` (`BigDecimal`, escala 2, `HALF_UP`).
- Duplicidade de `ProductId` na origem dos dados de estoque.
  - Solução: garantir unicidade na persistência e na borda de infraestrutura; o domínio `Stock` recebe `Map<ProductId, StockBalance>` já normalizado.
- Expansão de escopo durante o ciclo.
  - Solução: executar uma fatia por vez com contrato e critério de pronto explícitos.
- Divergência entre implementação e documentação.
  - Solução: atualizar `CODEX`, modelo de domínio e ADR ao fim de cada ciclo/fase.
- Regressão de nomenclatura de pacote para Value Objects.
  - Solução: padrão oficial consolidado em `com.pdvjava.domain.vo`; não recriar variações paralelas no `server-local`.
- Estrutura criada sem código correspondente.
  - Solução: evitar considerar pacote criado como funcionalidade pronta; validar por testes e classes concretas.

### 3A.7 Design patterns do projeto

- Clean Architecture (direção de dependências para dentro).
- Domain-Driven Design tático (Aggregate Root, Entities, Value Objects).
- Use Case/Application Service para orquestração sem deslocar regra crítica do domínio.
- Testes de domínio como documentação executável.

### 3A.8 Checklist pós-implementação

1. Regra implementada na camada correta.
2. Invariantes protegidas por API pública.
3. Testes cobrindo cenário válido, inválido e borda.
4. Sem `float`/`double` no domínio.
5. Sem anotação de framework no domínio.
6. `mvn -B -ntp verify` em verde.
7. `mvn -B -ntp spotless:check` em verde.
8. Revisão de consistência entre pacote/documentação (nomes e contratos).
9. Documentação atualizada (`CODEX`, `DOMAIN_MODEL_V1`, ADR quando aplicável).
10. Próximo ciclo recomendado explicitado no fechamento de sessão.

---

## 4. Escopo da Fase Atual (Fase 1)

Construir domínio puro no `server-local` com testes, sem dependência de framework:

- Value Objects: `Money`, `Quantity`, `Percentage`, `PaymentMethod` e `StockBalance`
- Entidades iniciais: `Cart`, `Sale`, `SaleItem`, `Product`, `Stock`
- Invariantes críticas de carrinho, venda, pagamento e estoque
- Testes unitários de domínio como documentação executável
- Fora de escopo no V1: `CashRegister` e cancelamento/estorno

---

## 5. Próximos Passos Imediatos

1. Executar o ciclo 2 de `Cart`: merge de quantidade por `ProductId`, remoção de item e recálculo de total.
2. Executar o ciclo 3 de `Cart`: `startSale()` e bloqueio de mutações após `CHECKOUT_STARTED`.
3. Implementar `Sale` como agregado transacional derivado de um `Cart` válido já bloqueado para edição.
4. Expandir a cobertura das invariantes críticas com JUnit para o fluxo `Cart -> Sale -> pagamento`.
5. Preparar a futura consistência entre `Sale` e `Stock` no fechamento da venda.

Próxima fatia lógica recomendada:
1. `Cart`
   - somar quantidade quando o mesmo `ProductId` for adicionado novamente
   - remover item por `ProductId`
   - recalcular `totalAmount` após merge e remoção
   - preservar o carrinho editável como estado persistível/recuperável do fluxo
2. `Cart`
   - implementar `startSale()`
   - bloquear mutações quando o checkout for iniciado
3. `Sale`
   - nascer a partir de um `Cart` válido já bloqueado
   - proteger transições de estado explícitas
   - validar pagamento e consistência com o total final

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

### Regra inegociável de mentoria

- O assistente NÃO escreve código por conta própria.
- O papel padrão do assistente é mentorar, revisar e orientar decisões técnicas.
- Qualquer implementação direta pelo assistente exige autorização explícita do autor no ciclo atual.

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

---

## 17. Fechamento de Sessão — 2026-03-31

- Concluído:
  - revisão dos Value Objects implementados em `domain/vo`
  - consolidação documental da estrutura vigente de pacotes do `server-local` em `com.pdvjava.*`
  - remoção de árvore vazia paralela `com.pdvjava.serverlocal.*`
  - evolução do contrato de `Money` para expor `value()` com retorno monetário normalizado
  - inclusão de testes de `Money` cobrindo leitura do valor normalizado e escala monetária
  - commit local da fatia de `Money`: `7f5c281` (`feat(domain): expose money normalized value`)

- Pendências:
  - iniciar a microfatia `ProductId`
  - após `ProductId`, iniciar a fatia `Product`
  - definir se `SaleId` será implementado logo após `ProductId` ou apenas quando `Sale` começar

- Riscos ativos:
  - risco de expandir escopo de `ProductId` para `Product` na mesma sessão
  - risco de criar identidade fraca com primitivo solto se `Product` avançar sem `ProductId`
  - risco de divergência entre documentação e código se novas APIs de VO forem adicionadas sem atualizar o modelo

- Próximo passo recomendado:
  - abrir a microfatia `ProductId` com contrato, testes planejados e implementação mínima

- Instrução de retorno (prompt padrão):
  - "Leia `docs/CODEX.md`, `docs/DOMAIN_ROADMAP.md`, `docs/DOMAIN_MODEL_V1.md`, `docs/CODING_RULES.md` e `docs/DOMAIN_DISCOVERY.md` e inicie o próximo ciclo pela microfatia `ProductId`."

---

## 18. Fechamento de Sessão — 2026-04-02

- Concluído:
  - leitura e revalidação do contexto operacional em `CODEX`, `DOMAIN_ROADMAP`, `DOMAIN_MODEL_V1`, `CODING_RULES` e `DOMAIN_DISCOVERY`
  - execução da microfatia `ProductId` no `server-local`
  - implementação de `ProductId` como Value Object com factory `of(Long)`, leitura por `value()` e igualdade semântica por valor
  - inclusão de testes cobrindo criação válida, `null`, zero, negativo e igualdade/diferença entre identificadores
  - commit local da microfatia `ProductId`: `d3d53ca` (`feat(domain): add ProductId value object`)
  - registro documental do fechamento da fatia anterior de `Money`: `d1ea808` (`docs(project): record money slice session closure`)

- Pendências:
  - iniciar a fatia `Product`
  - definir a API mínima de criação de `Product` com `ProductId`, `name` e `unitPrice`
  - implementar testes de `Product` antes da classe

- Riscos ativos:
  - risco de expandir a fatia `Product` para `Stock` na mesma sessão
  - risco de deixar `Product` com identidade fraca ou campos com visibilidade indevida
  - risco de introduzir validações implícitas em vez de invariantes explícitas na entidade

- Próximo passo recomendado:
  - abrir a microfatia `Product` com contrato mínimo: criação válida, `productId` obrigatório, `name` não nulo/não vazio e `unitPrice` maior que zero

- Instrução de retorno (prompt padrão):
  - "Leia `docs/CODEX.md`, `docs/DOMAIN_ROADMAP.md`, `docs/DOMAIN_MODEL_V1.md`, `docs/CODING_RULES.md` e `docs/DOMAIN_DISCOVERY.md` e inicie o próximo ciclo pela microfatia `Product`."

---

## 19. Fechamento de Sessão — 2026-04-12

- Concluído:
  - revisão e alinhamento da documentação central para introduzir `Cart` antes de `Sale` no V1
  - atualização de `DOMAIN_DISCOVERY`, `DOMAIN_MODEL_V1`, `DOMAIN_ROADMAP` e `CODEX` para refletir `Cart` como agregado persistível e recuperável em estado editável
  - correção de consistência documental em métodos de pagamento (`PIX`) e estados documentados de `Sale`
  - revisão da implementação inicial de `CartId`, `CartStatus` e `Cart`
  - validação do ciclo 1 de `Cart` após correção dos findings de review
  - execução de `mvn -B -ntp -pl server-local test` em verde

- Pendências:
  - executar o ciclo 2 da fatia `Cart`
  - implementar merge de quantidade por `ProductId`
  - implementar remoção de item por `ProductId`
  - recalcular `totalAmount` nos cenários de merge e remoção
  - preparar o ciclo 3 com `startSale()` e bloqueio de mutações

- Riscos ativos:
  - risco de implementar merge de item duplicado sem preservar uma única linha por `ProductId`
  - risco de recalcular `totalAmount` parcialmente e deixar o agregado inconsistente após remoção
  - risco de expandir o escopo de `Cart` para `Sale` antes de fechar os ciclos 2 e 3

- Próximo passo recomendado:
  - abrir a próxima sessão no ciclo 2 de `Cart`, começando pelos testes:
  - `should_sum_quantity_when_adding_item_with_same_product_id`
  - `should_keep_single_line_when_adding_item_with_same_product_id`
  - `should_recalculate_total_amount_when_adding_item_with_same_product_id`
  - `should_remove_item_when_product_id_exists_in_cart`
  - `should_recalculate_total_amount_when_removing_item`
  - `should_return_empty_cart_when_removing_last_item`
  - `should_throw_exception_when_removing_item_with_null_product_id`

- Instrução de retorno (prompt padrão):
  - "Leia `docs/CODEX.md`, `docs/DOMAIN_ROADMAP.md`, `docs/DOMAIN_MODEL_V1.md`, `docs/CODING_RULES.md` e `docs/DOMAIN_DISCOVERY.md` e inicie o ciclo 2 da fatia `Cart` pelos testes já definidos."
