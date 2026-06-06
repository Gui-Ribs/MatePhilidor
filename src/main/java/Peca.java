public class Peca {

	private final CorPeca cor;
	private final TipoPeca tipo;

	public Peca(CorPeca cor, TipoPeca tipo) {
		this.cor = cor;
		this.tipo = tipo;
	}

	public CorPeca getCor() {
		return cor;
	}
	public TipoPeca getTipo() {
		return tipo;
	}

	public String getSimbolo() {
		return tipo.getSimbolo(cor);
	}
	public String getNome() {
		return tipo.getNome();
	}
}
