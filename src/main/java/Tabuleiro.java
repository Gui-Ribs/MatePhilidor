public class Tabuleiro {

	private final int dimensao = 8;
	private final Casa[][] casas;
	private boolean reiBrancoEmXeque;
	private boolean reiPretoEmXeque;

	public Tabuleiro() {
		casas = new Casa[8][8];
		for (int lin = 0; lin < 8; lin++) {
			for (int col = 0; col < 8; col++) {
				casas[lin][col] = new Casa(new Posicao((char) ('a' + col), lin + 1));
			}
		}
		reiBrancoEmXeque = false;
		reiPretoEmXeque = false;
	}

	// Acesso

	public Casa getCasa(Posicao p) {
		if (p == null) {
			return null;
		}

		return casas[p.indiceLinha()][p.indiceColuna()];
	}
	public int getDimensao() {
		return dimensao;
	}

	// Operacoes

	public void adicionarPeca(Peca peca, Posicao pos) {
		Casa casa = getCasa(pos);
		if (casa != null)
			casa.setPeca(peca);
	}

	public void moverPeca(Movimento mov) {
		Casa origem = getCasa(mov.origem());
		Casa destino = getCasa(mov.destino());
		if (origem == null || destino == null || !origem.isOcupada())
			return;

		Peca pecaMovida = origem.getPeca();
		destino.setPeca(pecaMovida); // sobrescreve; peça anterior some do tabuleiro
		origem.removerPeca();
	}

	public void removerPeca(Posicao pos) {
		Casa casa = getCasa(pos);
		if (casa != null)
			casa.removerPeca();
	}

	// Estado de xeque

	public boolean getReiEmXeque(CorPeca cor) {
		return cor == CorPeca.BRANCA ? reiBrancoEmXeque : reiPretoEmXeque;
	}

	public void setReiEmXeque(CorPeca cor, boolean reiEmXeque) {
		if (cor == CorPeca.BRANCA)
			reiBrancoEmXeque = reiEmXeque;
		else
			reiPretoEmXeque = reiEmXeque;
	}
}
