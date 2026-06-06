# Mate de Philidor — Cenário Lúdico

## Compilar e executar

Via Makefile:

```bash
make run      # compila e abre
make build    # compila e empacota
make format   # formata o código
make check    # verifica formatação sem alterar arquivos
```

Ou diretamente pelo Gradle Wrapper:

```bash
./gradlew run
```

## Fluxo do Cenário Lúdico

| Etapa | Jogador | Lance  | Movimento |
|-------|---------|--------|-----------|
| 0 → 1 | Brancas | Cf7+   | e5 → f7   |
| 1 → 2 | Pretas  | Rg8    | h8 → g8   |
| 2 → 3 | Brancas | Ch6++  | f7 → h6   |
| 3 → 4 | Pretas  | Rh8    | g8 → h8   |
| 4 → 5 | Brancas | Dg8+!  | d5 → g8   |
| 5 → 6 | Pretas  | Txg8   | f8 → g8   |
| 6 → 7 | Brancas | Cf7#   | h6 → f7   |

Xeque-mate: Rei em h8 sufocado por Torre (g8), Torre (g7) e Peão (h7);
Cavalo em f7 aplica o mate.