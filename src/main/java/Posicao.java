public record Posicao(char coluna, int linha) {

	public Posicao {
		coluna = Character.toLowerCase(coluna);

		if (coluna < 'a' || coluna > 'h') {
			throw new IllegalArgumentException("Coluna deve estar entre 'a' e 'h'.");
		}

		if (linha < 1 || linha > 8) {
			throw new IllegalArgumentException("Linha deve estar entre 1 e 8.");
		}
	}

	public int indiceColuna() {
		return coluna - 'a';
	}

	public int indiceLinha() {
		return linha - 1;
	}

	@Override
	public String toString() {
		return "" + coluna + linha;
	}
}
