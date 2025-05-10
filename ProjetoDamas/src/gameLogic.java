record movePosition(int line1, int column1, int line2, int column2, boolean moveOrEat, boolean leftOrRight) {
}						//se moveOrEat for true é move, se leftOrRight for true é esquerda
record position(int line, int column) {
}
class gameLogic {
	
	private boolean whiteTurn;
	private int size;
	private int numOfPieces;
	private int[][] gameBoard;
	private int numOfPlays;
	private int white;
	private int black;
	private int empty;
	private int numOfWhitePieces;
	private int numOfBlackPieces;
	private movePosition[] plays;
	
	gameLogic(){									//construtor default que faz tabuleiro 8x8
		this.size = 8;
		gameBoard = new int[size][size];
		numOfPieces = 12;
		numOfWhitePieces = 0;
		numOfBlackPieces = 0;
		fillBoard();
		whiteTurn = true;
		numOfPlays = 0;
		empty = 0;
		white = 1;
		black = 2;
		plays = new movePosition[numOfPossiblePlays()];
	}
	gameLogic(int size, int numOfPieces){			//contrutor geral que se escolhe as proporções do tabuleiro
		assert(numOfPieces*2 <= (size*size)/2):"o número de peças dos dois jogadores juntos tem de ser menor que o número de casas jogárveis";
		assert(size % 2 == 0 && size > 0):"o tamnho do tabuleiro tem de ser número par por par e maior do que zero";
		this.size = size;
		this.numOfPieces = numOfPieces;
		whiteTurn = true;
		numOfPlays = 0;
		empty = 0;
		white = 1;
		black = 2;
		numOfWhitePieces = 0;
		numOfBlackPieces = 0;
		gameBoard = new int[size][size];
		fillBoard();
		plays = new movePosition[numOfPossiblePlays()];
	}

	gameLogic(int[][] loadedBoard, boolean loadedTurn, int loadedNumOfPlays, int loadedSize, int loadedNumOfWhitePieces, int loadedNumOfBlackPieces) {		//construtor para quando se começa o jogo guardado
		this.gameBoard = loadedBoard;
		this.whiteTurn = loadedTurn;
		this.numOfPlays = loadedNumOfPlays;				//possivel erro será falta de variaveis aqui
		this.size = loadedSize;
		this.numOfWhitePieces = loadedNumOfWhitePieces;
		this.numOfBlackPieces = loadedNumOfBlackPieces;
		empty = 0;
		white = 1;
		black = 2;
	}
	
	int untilLineForBlack() {					//dá até que linha se podem colocar peças de acordo com a sua quantidade
		return numOfPieces / (size / 2) + 1;	//para as brancas tem de ser size-untilLineForBlack()
	}
	void fillBoard() {
		for(int l = size-1; l >= size-untilLineForBlack(); l--) {
			for(int c = size-1; c >= 0; c--) {										//colocar as peças brancas (=1) na matriz do tabuleiro ao contrário
				if(isPlayablePosition(l,c) && numOfWhitePieces < numOfPieces) {
					gameBoard[l][c] = 1;
					numOfWhitePieces++;
				}
			}																		//locais com valor 0 não têm peças
		}
		for(int l = 0; l < untilLineForBlack(); l++) {
			for(int c = 0; c < size; c++) {											//colocar as peças pretas (=2) na matriz do tabuleiro
				if(isPlayablePosition(l,c) && numOfBlackPieces < numOfPieces) {
					gameBoard[l][c] = 2;
					numOfBlackPieces++;
				}
			}
		}
	}
	int numOfPossiblePlays() {					//dá o número de jogadas possiveis
		int numOfPossiblePlays = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(isThereMandatoryToEat() >= 1) {
					if(isMandatoryToEatLeft(i,j)) {
						numOfPossiblePlays++;
					}
					if(isMandatoryToEatRight(i,j)) {
						numOfPossiblePlays++;
					}
				}
				else {
					if(canMoveLeft(i,j)) {
						numOfPossiblePlays++;
					}
					if(canMoveRight(i,j)) {
						numOfPossiblePlays++;
					}
				}
			}
		}
		return numOfPossiblePlays;
	}
	boolean whiteTurn() {		//verifica de quem é a vez de jogar: se for true são as brancas senão são as pretas
		return whiteTurn;
	}
	int finalEatPositionTurn() {	//para fazer corretamente a seleção de jogadas na possiblePlays()
		return whiteTurn?-2:2;
	}								//distinção do movimentos das peças (pela soma ou subtração nas linhas
	int finalMovePositionTurn() {
		return whiteTurn?-1:1;		//para fazer corretamente a seleção de jogadas na possiblePlays()
	}
	int numOfWhitePieces() {
		return numOfWhitePieces;
	}
	int numOfBlackPieces() {
		return numOfBlackPieces;
	}
	int size() {				//dá o size do tabuleiro
		return size;
	}
	int numOfPlays() {					//dá o número de jogadas já efetuadas
		return numOfPlays;
	}
	int gameBoard(int line, int column){		//dá acesso ao valor numa determinada linha e coluna da matriz do tabuleiro do jogo
		return gameBoard[line][column];
	}
	boolean isThereWhite(int line, int column) {		//verifica se naquele espaço está uma peça branca
		return gameBoard[line][column] == white;
	}
	boolean isThereBlack(int line, int column) {		//verifica para as pretas
		return gameBoard[line][column] == black;
	}
	
	void possiblePlays() {	
		int index = 0;			//se moveOrEat for true é move, se leftOrRight for true é esquerda e se whiteOrBlack for true é branca
		plays = new movePosition[numOfPossiblePlays()];
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(isThereMandatoryToEat() >= 1) {
					if(isMandatoryToEatLeft(i,j)) {
						plays[index] = new movePosition(i, j, i+finalEatPositionTurn(), j-2, false, true);
						index++;
					}
					if(isMandatoryToEatRight(i,j)) {
						plays[index] = new movePosition(i, j, i+finalEatPositionTurn(), j+2, false, false);
						index++;
					}
				}
				else {
					if(canMoveLeft(i,j)) {
						plays[index] = new movePosition(i, j, i+finalMovePositionTurn(), j-1, true, true);
						index++;
					}
					if(canMoveRight(i,j)) {
						plays[index] = new movePosition(i, j, i+finalMovePositionTurn(), j+1, true, false);
						index++;
					}
				}
			}
		}
	}

	boolean isTherePiece(int line, int column) {			//verifica se tem alguma peça na posição
		if(isPlayablePosition(line,column)) {	
			if(isThereWhite(line,column) || isThereBlack(line,column)) {
				return true;
			}
		}
		return false;
	}
	
	boolean isPlayablePosition(int line, int column) {		//verifica se é uma casa preta ou seja se são as únicas casas onde as peças podem andar
		if(line % 2 == 0) {
			if(column % 2 == 1)
				return true;
		}
		if(line % 2 == 1) {
			if(column % 2 == 0)
				return true;
		}
		return false;
	}
	
	int isThereMandatoryToEat() {			//verifica quantas peças do turno têm a obrigação de comer
		int piecesThatCanEat = 0;
		for(int i = 0; i < gameBoard.length; i++) {
			for(int j = 0; j < gameBoard.length; j++) {
				if(whiteTurn && isThereWhite(i,j) && isMandatoryToEat(i,j)) {
					piecesThatCanEat++;
				}
				if(!whiteTurn && isThereBlack(i,j) && isMandatoryToEat(i,j)) {
					piecesThatCanEat++;
				}
			}
		}
		return piecesThatCanEat;
	}
	
	boolean isMandatoryToEat(int line, int column) {									//verifica se é obrigatório numa determinada posição comer outras peças
		if(isMandatoryToEatLeft(line, column) || isMandatoryToEatRight(line, column)) {
			return true;
		}
		return false;
	}
	
	boolean isMandatoryToEatLeft(int line, int column) {					//verifica se é obrigatório comer a esquerda
		if(column > 1) {
			if(line > 1 && whiteTurn) {
				if(isThereWhite(line,column)) {
					if(isThereBlack(line-1,column-1) && !isTherePiece(line-2,column-2)) {	//a branca comer a esquerda
						return true;
					}
				}
			}
			else if(line < gameBoard.length - 2 && !whiteTurn) {
				if(isThereBlack(line,column)) {
					if(isThereWhite(line+1,column-1) && !isTherePiece(line+2,column-2)) {	//a preta comer a esquerda
						return true;
					}
				}
			}
		}
		return false;
	}
	
	boolean isMandatoryToEatRight(int line, int column) {					//verifica se é obrigatório comer a direita
		if(column < gameBoard.length - 2) {
			if(line > 1 && whiteTurn) {	
				if(isThereWhite(line,column)) {
					if(isThereBlack(line-1,column+1) && !isTherePiece(line-2,column+2)) {	//a branca comer a direita
						return true;
					}
				}
			}
			if(line < gameBoard.length - 2 && !whiteTurn) {
				if(isThereBlack(line,column)) {
					if(isThereWhite(line+1,column+1) && !isTherePiece(line+2,column+2)) {	//a preta comer a direita
						return true;
					}
				}
			}
		}
		return false;
	}
	
	boolean canMoveLeft(int line, int column) {			//verifica se dá para mover para a esquerda
	    if (column > 0) {
	    	if(line > 0 && whiteTurn) {
	    		if(isThereWhite(line,column)) {
		            if (!isTherePiece(line - 1, column - 1)) {
		                return true;
		            }
		        }
	    	}
	    	if(line < gameBoard.length - 1 && !whiteTurn) {
		        if(isThereBlack(line,column)) {
		            if (!isTherePiece(line + 1, column - 1)) {
		                return true;
		            }
		        }
	    	}
	    }
	    return false;
	}
	
	boolean canMoveRight(int line, int column) {		//verifica se dá para mover para a direita
	    if (column < gameBoard.length - 1) {
	    	if(line > 0 && whiteTurn) {
	    		if (isThereWhite(line,column)) {
		            if (!isTherePiece(line - 1, column + 1)) {
		                return true;
		            }
		        }
	    	}
	    	if(line < gameBoard.length - 1 && !whiteTurn) {
		        if(isThereBlack(line,column)) {
		            if (!isTherePiece(line + 1, column + 1)) {
		                return true;
		            }
		        }
	    	}
	    }
	    return false;
	}
	
	boolean pieceIsPlayable(int line, int column) {		//verifica se a peça é jogável
		if(isPlayablePosition(line,column)) {
			if(isTherePiece(line,column)) {
				if(canMoveLeft(line,column) || canMoveRight(line,column) || isMandatoryToEat(line,column))
					return true;
			}
		}
		return false;
	}
	
	position positionInBetween(int line1, int column1, int line2, int column2) {
		return new position((line1+line2)/2,(column1+column2)/2);
	}
	void movePiece(int line1, int column1, int line2, int column2) {		//faz movimento para a esquerda ou direita na diagonal
		if(isPlayablePosition(line2,column2)) {
			if(!isTherePiece(line2,column2)) {	
				if(whiteTurn) {
					gameBoard[line2][column2] = white;
					gameBoard[line1][column1] = empty;
				}
				else {	
					gameBoard[line2][column2] = black;
					gameBoard[line1][column1] = empty;
				}
				whiteTurn = !whiteTurn;
				numOfPlays++;
			}
		}
	}
	
	void eatPiece(int line1, int column1, int line2, int column2) {			//come uma peça na diagonal
		if(isPlayablePosition(line2, column2)) {
			if(!isTherePiece(line2, column2)) {
				if(whiteTurn) {
					gameBoard[line2][column2] = white;
					gameBoard[positionInBetween(line1,column1,line2,column2).line()][positionInBetween(line1,column1,line2,column2).column()] = empty;
					gameBoard[line1][column1] = empty;
					numOfBlackPieces--;
				}
				else {
					gameBoard[line2][column2] = black;
					gameBoard[positionInBetween(line1,column1,line2,column2).line()][positionInBetween(line1,column1,line2,column2).column()] = empty;
					gameBoard[line1][column1] = empty;
					numOfWhitePieces--;
				}
				whiteTurn = !whiteTurn;
				numOfPlays++;
			}
		}
	}
	
	int hasWon() {
		if(numOfPossiblePlays() == 0) {					//se o jogo ainda não acabou dá 0, se as brancas ganharem dá 1, se as pretas ganharem dá 2, se houver empate dá 3
			if(numOfWhitePieces > numOfBlackPieces) {
				return 1;
			}
			if(numOfWhitePieces < numOfBlackPieces) {
				return 2;
			}
			if(numOfWhitePieces == numOfBlackPieces) {
				return 3;
			}
		}
		return 0;
	}
	
	void play(int line1, int column1, int line2, int column2) {		//efetua uma jogada caso ainda ninguém tiver ganho:
		if(hasWon() == 0 && isPlayablePosition(line2, column2)) {	//se for obrigatório comer uma peça é comida, senão tem a opção de mexer as peças na diagonal da esquerda ou direita	
			if(isThereMandatoryToEat() >= 1) {
				if(line1 + finalEatPositionTurn() == line2 && column1 - 2 == column2) {	//comer a esquerda
					eatPiece(line1, column1, line2,column2);
				}
				if(line1 + finalEatPositionTurn() == line2 && column1 + 2 == column2) {	//comer a direita
					eatPiece(line1, column1, line2,column2);
				}
			}
			else if(line1 + finalMovePositionTurn() == line2 && column1 - 1 == column2) {	//move a esquerda
				movePiece(line1, column1, line2, column2);
			}
			else if(line1 + finalMovePositionTurn() == line2 && column1 + 1 == column2) {	//move a direita
				movePiece(line1, column1, line2, column2);
			}
		}
	}
	
	int randomIndex() {								//dá um index random
		return (int)(Math.random()*(plays.length));	
	}
	void randomPlay() {								//efetua uma jogada random
		possiblePlays();							//atualiza / chama as jogadas possiveis
		if(hasWon() == 0) {
			if(isThereMandatoryToEat() >= 1) {
				movePosition play = plays[randomIndex()];
				while(play.moveOrEat() == true) {
					play = plays[randomIndex()];
				}
				play(play.line1(), play.column1(), play.line2(), play.column2());
			}
			else{
				movePosition play = plays[randomIndex()];
				while(play.moveOrEat() == false) {
					play = plays[randomIndex()];
				}
				play(play.line1(), play.column1(), play.line2(), play.column2());
			}
		}
	}
}