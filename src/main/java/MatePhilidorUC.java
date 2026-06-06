public class MatePhilidorUC {

	private static final int NUM_LANCES = 7;

	// Posições do cenario

	private static final Posicao E5 = new Posicao('e', 5);
	private static final Posicao D5 = new Posicao('d', 5);
	private static final Posicao F7 = new Posicao('f', 7);
	private static final Posicao H6 = new Posicao('h', 6);
	private static final Posicao G8 = new Posicao('g', 8);
	private static final Posicao H8 = new Posicao('h', 8);
	private static final Posicao G7 = new Posicao('g', 7);
	private static final Posicao F8 = new Posicao('f', 8);
	private static final Posicao H7 = new Posicao('h', 7);
	private static final Posicao G1 = new Posicao('g', 1);
	private static final Posicao G2 = new Posicao('g', 2);
	private static final Posicao H2 = new Posicao('h', 2);
	private static final Posicao B2 = new Posicao('b', 2);
	private static final Posicao B3 = new Posicao('b', 3);
	private static final Posicao F2 = new Posicao('f', 2);

	private final Tabuleiro tabuleiro;
	private int etapaAtual;
	private EstadoPartida estado;

	public MatePhilidorUC() {
		this.tabuleiro = new Tabuleiro();
		this.etapaAtual = 0;
		this.estado = EstadoPartida.EM_ANDAMENTO;
		carregarCenarioInicial();
	}

	private void carregarCenarioInicial() {
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.CAVALO), E5);
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.DAMA), D5);
		tabuleiro.adicionarPeca(new Peca(CorPeca.PRETA, TipoPeca.DAMA), B2);
		tabuleiro.adicionarPeca(new Peca(CorPeca.PRETA, TipoPeca.REI), H8);
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.REI), G1);
		tabuleiro.adicionarPeca(new Peca(CorPeca.PRETA, TipoPeca.PEAO), G7);
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.PEAO), G2);
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.PEAO), H2);
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.PEAO), B3);
		tabuleiro.adicionarPeca(new Peca(CorPeca.BRANCA, TipoPeca.PEAO), F2);
		tabuleiro.adicionarPeca(new Peca(CorPeca.PRETA, TipoPeca.TORRE), F8);
		tabuleiro.adicionarPeca(new Peca(CorPeca.PRETA, TipoPeca.PEAO), H7);
	}

	// Pré-condições

	public boolean verificarPreCondicoes() {
		Casa casaRei = tabuleiro.getCasa(H8);
		Casa casaCavalo = tabuleiro.getCasa(E5);
		Casa casaDama = tabuleiro.getCasa(D5);
		return pecaDoTipo(casaRei, TipoPeca.REI, CorPeca.PRETA)
				&& pecaDoTipo(casaCavalo, TipoPeca.CAVALO, CorPeca.BRANCA)
				&& pecaDoTipo(casaDama, TipoPeca.DAMA, CorPeca.BRANCA);
	}

	private boolean pecaDoTipo(Casa casa, TipoPeca tipo, CorPeca cor) {
		return casa != null && casa.isOcupada() && casa.getPeca().getTipo() == tipo && casa.getPeca().getCor() == cor;
	}

	// Execução

	public Movimento getProximoMovimento() {
		return switch (etapaAtual) {
			case 0 -> new Movimento(E5, F7);
			case 1 -> new Movimento(H8, G8);
			case 2 -> new Movimento(F7, H6);
			case 3 -> new Movimento(G8, H8);
			case 4 -> new Movimento(D5, G8);
			case 5 -> new Movimento(F8, G8);
			case 6 -> new Movimento(H6, F7);
			default -> null;
		};
	}

	public void executarProximoMovimento(Movimento mov) {
		if (estado != EstadoPartida.EM_ANDAMENTO)
			return;
		if (etapaAtual >= NUM_LANCES)
			return;

		tabuleiro.moverPeca(mov);

		boolean reiEmXeque = (etapaAtual % 2 == 0);
		tabuleiro.setReiEmXeque(CorPeca.PRETA, reiEmXeque);

		etapaAtual++;

		if (etapaAtual == NUM_LANCES) {
			estado = EstadoPartida.ENCERRADA;
		}
	}

	public void abortar() {
		estado = EstadoPartida.ABORTADA;
	}

	// Verificações

	public boolean verificarXeque() {
		return tabuleiro.getReiEmXeque(CorPeca.PRETA);
	}

	public boolean verificarXequeMate() {
		return estado == EstadoPartida.ENCERRADA && verificarXeque();
	}

	public boolean isXequeDuplo() {
		return etapaAtual == 3;
	}

	// Consultas para a Boundary

	public Peca getPecaEm(Posicao posicao) {
		Casa casa = tabuleiro.getCasa(posicao);
		return casa == null ? null : casa.getPeca();
	}

	public String getDescricaoEtapaAtual() {
		return getDescricaoEtapa();
	}

	public String getMensagemXeque() {
		if (!verificarXeque())
			return "";
		if (verificarXequeMate())
			return "Rei Preto em XEQUE-MATE!";
		if (isXequeDuplo())
			return "Rei Preto em XEQUE DUPLO!";
		return "Rei Preto em XEQUE!";
	}

	public String getMensagemFinal() {
		return """
				PARTIDA ENCERRADA -- BRANCAS VENCEM!

				Rei Preto sufocado pelas proprias peças:
				  * Torre (g8) bloqueia a casa g8
				  * Peao  (g7) bloqueia a casa g7
				  * Peao  (h7) bloqueia a casa h7
				  * Cavalo (f7) da xeque-mate em h8

				MATE DE PHILIDOR EXECUTADO! 
			""";
	}

	// Numero total de lances do roteiro do Mate de Philidor.

	public int getNumeroTotalLances() {
		return NUM_LANCES;
	}

	public int getEtapaAtual() {
		return etapaAtual;
	}
	public EstadoPartida getEstado() {
		return estado;
	}

	// Interno

	private String getDescricaoEtapa() {
		return switch (etapaAtual) {
			case 0 -> "Posicao inicial -- aguardando Lance 1 das Brancas (Cf7+)";
			case 1 -> "Cf7+ executado  -- aguardando resposta das Pretas (Rg8)";
			case 2 -> "Rg8 executado   -- aguardando Lance 2 das Brancas (Ch6++)";
			case 3 -> "Ch6++ executado -- aguardando resposta das Pretas (Rh8)";
			case 4 -> "Rh8 executado   -- aguardando Lance 3 das Brancas (Dg8+!)";
			case 5 -> "Dg8+! executado -- aguardando resposta das Pretas (Txg8)";
			case 6 -> "Txg8 executado  -- aguardando Lance 4 das Brancas (Cf7#)";
			case 7 -> "Cf7# executado  -- XEQUE-MATE! Mate de Philidor!";
			default -> "Estado indefinido";
		};
	}
}
