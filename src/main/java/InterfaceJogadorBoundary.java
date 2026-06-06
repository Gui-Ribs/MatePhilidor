import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class InterfaceJogadorBoundary extends Application {

	private static final int DIMENSAO = 8;
	private static final double TAM_CASA = 72;
	private static final double TAM_IMAGEM_PECA = TAM_CASA - 10;

	private static final String COR_CLARA = "#f0d9b5";
	private static final String COR_ESCURA = "#b58863";
	private static final String CAMINHO_IMAGENS = "/img/";

	private static final String[] TITULOS_ETAPA = {"Lance 1 -- Brancas (objetivo: xeque em h8)",
			"Lance 1 -- Pretas (resposta forçada)", "Lance 2 -- Brancas (objetivo: xeque duplo)",
			"Lance 2 -- Pretas (resposta forçada)", "Lance 3 -- Brancas (sacrifício de Dama)",
			"Lance 3 -- Pretas (resposta forçada)", "Lance 4 -- Brancas (lance final: xeque-mate)"};

	private static final String[] EXPLICACOES_LANCE = {
			"Brancas: Cavalo e5 -> f7 [Cf7+]. O cavalo em f7 ataca h8; o rei preto está em xeque.",
			"Pretas: Rei h8 -> g8 [Rg8]. Com h7 e g7 bloqueadas, o rei é forçado a fugir para g8.",
			"Brancas: Cavalo f7 -> h6 [Ch6++]. Xeque duplo: o cavalo participa do ataque e a dama pressiona a linha decisiva.",
			"Pretas: Rei g8 -> h8 [Rh8]. O rei retorna para h8 por falta de casas úteis.",
			"Brancas: Dama d5 -> g8 [Dg8+!]. Sacrifício de dama: o rei não pode capturar porque o cavalo em h6 cobre g8.",
			"Pretas: Torre f8 -> g8 [Txg8]. A torre captura a dama, mas o rei fica sufocado.",
			"Brancas: Cavalo h6 -> f7 [Cf7#]. Xeque-mate: o rei preto está bloqueado pelas próprias peças."};

	private static final Opcao[][] OPCOES_ETAPA = {
			{new Opcao("Cf7+  (Cavalo e5 -> f7) -- xeque", true, null),
					new Opcao("Dh5  (Dama d5 -> h5)", false, "Dh5 não executa o Mate de Philidor."),
					new Opcao("Ce4  (Cavalo e5 -> e4)", false, "Cavalo em e5 não pode ir para e4.")},
			{new Opcao("Confirmar Rg8  (Rei h8 -> g8) -- única fuga", true, null)},
			{new Opcao("Ch6++  (Cavalo f7 -> h6) -- xeque duplo", true, null),
					new Opcao("Ce5  (Cavalo f7 -> e5)", false, "Ce5 recua o cavalo e perde a iniciativa."),
					new Opcao("Dg8+  (Dama d5 -> g8)", false, "Dg8+ é prematuro: o cavalo ainda não cobre g8.")},
			{new Opcao("Confirmar Rh8  (Rei g8 -> h8) -- única fuga", true, null)},
			{new Opcao("Dg8+!  (Dama d5 -> g8) -- sacrifício", true, null),
					new Opcao("Df5  (Dama d5 -> f5)", false, "Df5 não gera mate. O padrão exige Dg8."),
					new Opcao("Cf7  (Cavalo h6 -> f7)", false, "Cf7 sem o sacrifício de dama não fecha o mate.")},
			{new Opcao("Confirmar Txg8  (Torre f8 -> g8) captura a Dama", true, null)},
			{new Opcao("Cf7#  (Cavalo h6 -> f7) -- xeque-mate", true, null),
					new Opcao("Cg4  (Cavalo h6 -> g4)", false, "Cg4 não dá mate. O rei ainda teria recursos.")}};

	private static final Opcao[] SEM_OPCOES = {};

	private final MatePhilidorUC controlador = new MatePhilidorUC();
	private final Map<String, Image> cacheImagens = new HashMap<>();

	private GridPane tabuleiroGrid;
	private VBox painelOpcoes;
	private Label rotuloStatus;

	private record Opcao(String rotulo, boolean correta, String explicacao) {
	}

	@Override
	public void start(Stage stage) {
		BorderPane raiz = new BorderPane();

		rotuloStatus = new Label();
		rotuloStatus.setWrapText(true);
		rotuloStatus.setPadding(new Insets(12));
		rotuloStatus.setFont(Font.font("System", FontWeight.BOLD, 14));
		raiz.setTop(rotuloStatus);

		tabuleiroGrid = new GridPane();
		tabuleiroGrid.setAlignment(Pos.CENTER);
		tabuleiroGrid.setPadding(new Insets(12));
		raiz.setCenter(tabuleiroGrid);

		painelOpcoes = new VBox(10);
		painelOpcoes.setPadding(new Insets(12));
		painelOpcoes.setPrefWidth(320);
		raiz.setRight(painelOpcoes);

		Label legenda = new Label(
				"Clique na opção correta para executar cada lance do Mate de Philidor. Opções incorretas explicam o erro e não movem peças.");
		legenda.setWrapText(true);
		legenda.setPadding(new Insets(10, 12, 12, 12));
		raiz.setBottom(legenda);

		Scene cena = new Scene(raiz, 1000, 800);
		stage.setTitle("Mate de Philidor");
		stage.setScene(cena);
		stage.show();
		stage.centerOnScreen();

		iniciarPartida();
	}

	private void iniciarPartida() {
		desenharTabuleiro();
		atualizarStatus();

		if (!controlador.verificarPreCondicoes()) {
			alerta(AlertType.ERROR, "Pré-condições não satisfeitas",
					"A posição inicial não corresponde ao Mate de Philidor. O caso de uso foi abortado.");

			painelOpcoes.getChildren().clear();
			painelOpcoes.getChildren().add(new Label("Interação desabilitada."));
			return;
		}

		desenharOpcoes();
	}

	private void desenharTabuleiro() {
		tabuleiroGrid.getChildren().clear();

		for (int rank = DIMENSAO; rank >= 1; rank--) {
			for (int colIdx = 0; colIdx < DIMENSAO; colIdx++) {
				char coluna = (char) ('a' + colIdx);
				Posicao pos = new Posicao(coluna, rank);

				StackPane casa = criarCasa(colIdx, rank);

				Peca peca = controlador.getPecaEm(pos);
				if (peca != null) {
					ImageView imagemPeca = criarImagemPeca(peca);

					if (imagemPeca != null) {
						casa.getChildren().add(imagemPeca);
					}
				}

				tabuleiroGrid.add(casa, colIdx, DIMENSAO - rank);
			}
		}
	}

	private StackPane criarCasa(int colIdx, int rank) {
		StackPane casa = new StackPane();
		casa.setPrefSize(TAM_CASA, TAM_CASA);

		boolean clara = (colIdx + rank) % 2 != 0;
		String cor = clara ? COR_CLARA : COR_ESCURA;

		casa.setStyle("-fx-background-color: " + cor + ";");

		return casa;
	}

	private ImageView criarImagemPeca(Peca peca) {
		String arquivo = nomeArquivo(peca);

		if (arquivo == null) {
			return null;
		}

		Image imagem = cacheImagens.computeIfAbsent(arquivo, this::carregarImagem);

		if (imagem == null) {
			return null;
		}

		ImageView view = new ImageView(imagem);
		view.setPreserveRatio(true);
		view.setSmooth(true);
		view.setFitWidth(TAM_IMAGEM_PECA);
		view.setFitHeight(TAM_IMAGEM_PECA);

		return view;
	}

	private Image carregarImagem(String nomeArquivo) {
		InputStream stream = getClass().getResourceAsStream(CAMINHO_IMAGENS + nomeArquivo);

		if (stream == null) {
			System.err.println("Imagem não encontrada: " + CAMINHO_IMAGENS + nomeArquivo);
			return null;
		}

		return new Image(stream);
	}

	private String nomeArquivo(Peca peca) {
		return switch (peca.getCor()) {
			case BRANCA -> switch (peca.getTipo()) {
				case CAVALO -> "branca_cavalo.png";
				case DAMA -> "branca_dama.png";
				case REI -> "branca_rei.png";
				case PEAO -> "branca_peao.png";
				default -> null;
			};

			case PRETA -> switch (peca.getTipo()) {
				case REI -> "preta_rei.png";
				case TORRE -> "preta_torre.png";
				case PEAO -> "preta_peao.png";
				case DAMA -> "preta_dama.png";
				default -> null;
			};
		};
	}

	private void atualizarStatus() {
		int etapa = controlador.getEtapaAtual();
		int total = controlador.getNumeroTotalLances();

		String texto = "Estado: " + controlador.getEstado() + "   |   Etapa " + etapa + "/" + total + "\n"
				+ controlador.getDescricaoEtapaAtual();

		rotuloStatus.setText(texto);
	}

	private void desenharOpcoes() {
		painelOpcoes.getChildren().clear();

		int etapa = controlador.getEtapaAtual();

		Label titulo = new Label(tituloEtapa(etapa));
		titulo.setWrapText(true);
		titulo.setFont(Font.font("System", FontWeight.BOLD, 13));
		painelOpcoes.getChildren().add(titulo);

		for (Opcao opcao : opcoesEtapa(etapa)) {
			Button botao = criarBotaoOpcao(opcao);
			painelOpcoes.getChildren().add(botao);
		}

		Region espaco = new Region();
		VBox.setVgrow(espaco, Priority.ALWAYS);
		painelOpcoes.getChildren().add(espaco);

		Button sair = new Button("Sair");
		sair.setMaxWidth(Double.MAX_VALUE);
		sair.setOnAction(e -> sair());
		painelOpcoes.getChildren().add(sair);
	}

	private Button criarBotaoOpcao(Opcao opcao) {
		Button botao = new Button(opcao.rotulo());

		botao.setWrapText(true);
		botao.setMaxWidth(Double.MAX_VALUE);
		botao.setAlignment(Pos.CENTER_LEFT);
		botao.setOnAction(e -> escolher(opcao));

		return botao;
	}

	private void desabilitarOpcoes() {
		painelOpcoes.getChildren().forEach(no -> {
			if (no instanceof Button botao) {
				botao.setDisable(true);
			}
		});
	}

	private void sair() {
		controlador.abortar();
		atualizarStatus();
		desabilitarOpcoes();

		alerta(AlertType.INFORMATION, "Partida abortada", "Cenário Lúdico encerrado");
		Platform.exit();
	}

	private void escolher(Opcao opcao) {
		if (!opcao.correta()) {
			alerta(AlertType.ERROR, "Lance incorreto", opcao.explicacao());
			return;
		}

		int etapa = controlador.getEtapaAtual();
		Movimento movimento = controlador.getProximoMovimento();

		controlador.executarProximoMovimento(movimento);

		desenharTabuleiro();
		atualizarStatus();

		alerta(AlertType.INFORMATION, "Lance executado", explicacaoLance(etapa));

		if (controlador.verificarXequeMate()) {
			desabilitarOpcoes();

			alerta(AlertType.INFORMATION, "Xeque-mate!", controlador.getMensagemFinal());

			return;
		}

		if (controlador.verificarXeque()) {
			alerta(AlertType.WARNING, "Xeque", controlador.getMensagemXeque());
		}

		desenharOpcoes();
	}

	private void alerta(AlertType tipo, String titulo, String mensagem) {
		Alert alerta = new Alert(tipo);

		alerta.setTitle(titulo);
		alerta.setHeaderText(titulo);
		alerta.setContentText(mensagem);

		alerta.showAndWait();
	}

	private String tituloEtapa(int etapa) {
		if (!indiceValido(etapa, TITULOS_ETAPA.length)) {
			return "Etapa indefinida";
		}

		return TITULOS_ETAPA[etapa];
	}

	private Opcao[] opcoesEtapa(int etapa) {
		if (!indiceValido(etapa, OPCOES_ETAPA.length)) {
			return SEM_OPCOES;
		}

		return OPCOES_ETAPA[etapa];
	}

	private String explicacaoLance(int etapa) {
		if (!indiceValido(etapa, EXPLICACOES_LANCE.length)) {
			return "";
		}

		return EXPLICACOES_LANCE[etapa];
	}

	private static boolean indiceValido(int indice, int tamanho) {
		return indice >= 0 && indice < tamanho;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
