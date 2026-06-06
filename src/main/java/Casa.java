public class Casa {

	private final Posicao posicao;
	private Peca peca;

	public Casa(Posicao posicao) {
		this.posicao = posicao;
		this.peca = null;
	}

	public Posicao getPosicao() {
		return posicao;
	}
	public boolean isOcupada() {
		return peca != null;
	}
	public Peca getPeca() {
		return peca;
	}

	public void setPeca(Peca peca) {
		this.peca = peca;
	}
	public void removerPeca() {
		this.peca = null;
	}
}
