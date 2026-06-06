public record Movimento(Posicao origem, Posicao destino) {

	@Override
	public String toString() {
		return origem + "->" + destino;
	}
}
