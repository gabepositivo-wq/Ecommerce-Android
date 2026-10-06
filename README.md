# NovoEcommerce

App de e-commerce simples (trabalho escolar) em **Kotlin + Jetpack Compose + Gradle (Kotlin DSL)**,
seguindo o mockup: Home → Categoria → Detalhes do Produto → Carrinho → Pagamento.

## Como abrir
1. Android Studio (Ladybug 2024.2 ou mais novo) → **File > Open** → pasta `NovoEcommerce`.
2. Aguarde o **Gradle Sync** (baixa o Gradle 8.9 e as dependências; precisa de internet).
3. Rode no emulador ou celular (Android 8.0 / API 26+).

Se o sync reclamar de `gradle-wrapper.jar`/`gradlew` ausentes, rode uma vez no terminal:
`gradle wrapper --gradle-version 8.9` (ou deixe o Android Studio usar a distribuição do Gradle).

## Estrutura
- `app/src/main/java/com/example/novoecommerce/`
  - `MainActivity.kt`
  - `data/` – `Product`, `CartItem`, `ProductRepository` (dados fixos em memória)
  - `viewmodel/CartViewModel.kt` – carrinho, total, dados de pagamento/entrega
  - `navigation/AppNavigation.kt` – rotas
  - `ui/theme/` – tema escuro (preto e variações, texto branco)
  - `ui/components/` – cabeçalho, busca, card de produto etc.
  - `ui/screens/` – Home, Category, ProductDetail, Cart, Payment
