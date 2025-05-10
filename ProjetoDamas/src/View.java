import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
public class View {
	Board board;
	gameLogic model;
	private int marquedLine;
	private int marquedColumn;
	
	View(gameLogic model) {
		this.model = model;
		board = new Board(model.whiteTurn() ? "brancas jogam | nº de jogadas: "+model.numOfPlays() : "pretas jogam | nº de jogadas: "+model.numOfPlays(), model.size(), model.size(), 60);
		board.setIconProvider(this::icon);
		board.addMouseListener(this::clickPlay);
		board.setBackgroundProvider(this::background);
		board.addAction("aleatório", this::actionRandom);
		board.addAction("novo jogo", this::actionNewGame);
		board.addAction("gravar", this::actionSave);
		board.addAction("carregar", this::actionLoad);
		marquedLine = 0;
		marquedColumn = 0;
	}
	
	int marqLine(int line) {			//guarda o valor da linha marcada a amarelo
		return marquedLine = line;
	}
	int marqColumn(int column) {		//guarda o valor da coluna marcada a amarelo
		return marquedColumn = column;
	}
	int lineValue() {				//retorna a linha guardada
		return marquedLine;
	}
	int columnValue() {				//retorna a coluna guardada
		return marquedColumn;
	}
	void resetMarquedPosition() {
		marqLine(0);						//coloca a -1 as posições marcadas para limpar a posição a amarelo
		marqColumn(0);
	}
	
	String icon(int line, int column) {				//coloca a imagem da peça preta onde ela está na matriz e da peça branca o mesmo
		if (model.isThereWhite(line, column)) {
			return "white.png";
		}
		if (model.isThereBlack(line, column)) {
			return "black.png";
		}
		return null;
	}
	
	void clickPlay(int line, int column) {		//escolhe primeiro a posição que se quer jogar ao clicar na peça e depois joga com um segundo click
		if(model.hasWon() == 0) {
			if(lineValue() == 0 && columnValue() == 0) {
				if(model.whiteTurn()) {
					if(model.pieceIsPlayable(line, column) && model.isThereWhite(line, column)) {
						marqLine(line);
						marqColumn(column);
						return;
					}
				}
				if(!model.whiteTurn()) {
					if(model.pieceIsPlayable(line, column) && model.isThereBlack(line, column)) {
						marqLine(line);
						marqColumn(column);
						return;
					}
				}
			}
			else {
				model.play(lineValue(), columnValue(), line , column);
				board.setTitle(model.whiteTurn() ? "brancas jogam | nº de jogadas: "+model.numOfPlays() : "pretas jogam | nº de jogadas: "+model.numOfPlays());
				resetMarquedPosition();
			}
		}
	}
	
	Color background(int line, int column) {		//pinta as casas de preto caso sejam as casas onde se pode jogar e de branco as que não se pode
		if (model.hasWon() == 1) {
			board.setTitle("As brancas ganharam! | nº de jogadas: "+model.numOfPlays());
		}
		if (model.hasWon() == 2) {
			board.setTitle("As pretas ganharam! | nº de jogadas: "+model.numOfPlays());
		}
		if (model.hasWon() == 3) {
			board.setTitle("Ocorreu empate | nº de jogadas: "+model.numOfPlays());
		}
		if (line == lineValue() && column == columnValue()) {
			if(model.pieceIsPlayable(line, column)) {
				return StandardColor.YELLOW;		//ao clicar numa casa com uma peça jogável no seu turno torna a casa amarela
			}
		}
		if(model.gameBoard(line, column) == 0) {	//mostra jogadas possiveis
			if(model.isThereMandatoryToEat() >= 1) {	
				if(model.isMandatoryToEatLeft(lineValue(), columnValue()) && line == lineValue()+model.finalEatPositionTurn() && column == columnValue()-2) {
					return StandardColor.SILVER;
				}
				if(model.isMandatoryToEatRight(lineValue(), columnValue()) && line == lineValue()+model.finalEatPositionTurn() && column == columnValue()+2) {
					return StandardColor.SILVER;
				}
			}
			else {
				if(model.canMoveLeft(lineValue(), columnValue()) && line == lineValue()+model.finalMovePositionTurn() && column == columnValue()-1) {
					return StandardColor.SILVER;
				}
				if(model.canMoveRight(lineValue(), columnValue()) && line == lineValue()+model.finalMovePositionTurn() && column == columnValue()+1) {
					return StandardColor.SILVER;
				}
			}
		}
		if (model.isPlayablePosition(line, column)) {
			return StandardColor.BLACK;
		}
		return StandardColor.WHITE;
	}
	
	void actionRandom() {		//faz uma jogada aleatória dentro das regras
		if(model.hasWon() == 0) {
			model.randomPlay();
			board.setTitle(model.whiteTurn() ? "brancas jogam | nº de jogadas: "+model.numOfPlays() : "pretas jogam | nº de jogadas: "+model.numOfPlays());
		}
	}
	
	//faz um novo jogo escolhendo certos aspetos do novo jogo
	void actionNewGame() {
		int size = board.promptInt("Tamanho do tabuleiro? (?x?) *tem de ser número par e maior do que zero.");
		int numOfPieces = board.promptInt("Número de peças de cada jogador?"+"\n"+"*o conjunto das peças dos jogadores tem de ser menor ou igual que o número de posições pretas no tabuleiro.");
		if (size >= 0 && size % 2 == 0 && numOfPieces*2 <= (size*size)/2) {
			gameLogic newModel = new gameLogic(size,numOfPieces);
			View gui = new View(newModel);
			gui.start();
		}
	}
	
	void actionSave() {		//guarda um jogo em procedimento para ser jogado mais tarde
		try {
			String savedBoard = board.promptText("Nome do ficheiro?");
			PrintWriter saveBoard = new PrintWriter(new File(savedBoard));
			saveBoard.write(model.size()+" ");
			saveBoard.write(model.numOfPlays()+" ");
			saveBoard.write(model.numOfWhitePieces()+" ");
			saveBoard.write(model.numOfBlackPieces()+" ");
			if(model.whiteTurn()) {	
				saveBoard.write("true");
			}
			if(!model.whiteTurn()) {
				saveBoard.write("false");
			}
			saveBoard.println();
			for(int i = 0; i < model.size(); i++) {
				for(int j = 0; j < model.size(); j++) {
					saveBoard.write(model.gameBoard(i, j)+" ");;
				}
				saveBoard.println();
			}
			saveBoard.close();
		}
		catch(FileNotFoundException e) {
			board.showMessage("erro a escrever o ficheiro");
		}
	}
	
	void actionLoad() {		//carrega o jogo guardado para se poder continuar a jogar
		try {
			String loadFile = board.promptText("Nome do ficheiro que pretende carregar?");
			Scanner scanner = new Scanner(new File(loadFile));
			boolean loadedTurn = true;
			int loadedNumOfPlays = 0;
			int loadedSize = 0;
			int loadedNumOfWhitePieces = 0;
			int loadedNumOfBlackPieces = 0;

			loadedSize = scanner.nextInt();
			int[][] loadedBoard = new int[loadedSize][loadedSize];

			loadedNumOfPlays = scanner.nextInt();
			loadedNumOfWhitePieces = scanner.nextInt();
			loadedNumOfBlackPieces = scanner.nextInt();
			String loadedStringTurn = scanner.next();
			if(loadedStringTurn.equals("true")) {		//erro a ler o ficheiro. mete tudo com peça branca onde não tem nada e nas peças pretas mete nada
				loadedTurn = true;
			}
			if(loadedStringTurn.equals("false")) {
				loadedTurn = false;
			}
			for(int i = 0; i < loadedSize; i++) {
				for(int j = 0; j < loadedSize; j++) {
					int pieces = scanner.nextInt();
					if(pieces == 0) {
						loadedBoard[i][j] = 0;
					}
					if(pieces == 1) {
						loadedBoard[i][j] = 1;
					}
					if(pieces == 2) {
						loadedBoard[i][j] = 2;
					}
				}
			}
			scanner.close();
			gameLogic newModel = new gameLogic(loadedBoard,loadedTurn,loadedNumOfPlays,loadedSize,loadedNumOfWhitePieces,loadedNumOfBlackPieces);
			View gui = new View(newModel);
			gui.start();
		}
		catch(FileNotFoundException e) {
			board.showMessage("erro a ler o ficheiro");
		}
	}
		
	void start() {
		board.open();
	}

	public static void main(String[] args) {
		View gui = new View(new gameLogic());		//escolher o modo de jogo
		gui.start();
	}
}