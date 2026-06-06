public enum TipoPeca {

	CAVALO("C", "c", "Cavalo"), DAMA("D", "d", "Dama"), TORRE("T", "t", "Torre"), REI("R", "r", "Rei"), PEAO("P", "p",
			"Peao");

	private final String simboloBranco;
	private final String simboloPreto;
	private final String nome;

	TipoPeca(String simboloBranco, String simboloPreto, String nome) {
		this.simboloBranco = simboloBranco;
		this.simboloPreto = simboloPreto;
		this.nome = nome;
	}

	public String getSimbolo(CorPeca cor) {
		return cor == CorPeca.BRANCA ? simboloBranco : simboloPreto;
	}

	public String getNome() {
		return nome;
	}
}
