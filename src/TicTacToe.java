import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.*;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

public class TicTacToe extends GraphicsProgram {
	/**Board Width and Height */
	private static final int BoardHeight = 600;
	private static final int BoardWidth = 1000;
	/** Width of lines in game */
	private static final int LineWidth = 20;
	/** cell width and height */
	private static final int cellWidth = BoardWidth / 3 - LineWidth / 3 * 2;
	private static final int cellHeight = BoardHeight / 3 - LineWidth / 3 * 2 - BoardHeight / 24;
	/** Delay for checking  game ending conditions*/
	private static final int DELAY = 500;


	public void run() {
		addMouseListeners();
		addKeyListeners();
		this.setSize(BoardWidth, BoardHeight + BoardHeight / 8);
		do{
			setupMenu();
			while(!mouseClickedForBegin) pause(10);
			setupGame();
			if(isPLayingVSAI && !isPlayingCrosses) AIPlays();
			while(!hasPlayerWon && !hasAIWon && !isDraw) pause(DELAY);
			addWinScreen();
			mouseListenerIsActive = false;
			while(!mouseListenerIsActive) pause(DELAY);
		}while(true);

	}

	private void setupGame() {
		GLabel label = new GLabel("Click on the empty spot to place your mark");
		label.setFont("Arial-20");
		add(label,BoardWidth / 2 - label.getWidth() / 2 ,30);
		if(isPLayingVSAI) removeAIMenu();
		else{
			labelWhoseTurn = new GLabel("It`s 1st player`s turn");
			labelWhoseTurn.setFont("Arial-20");
			add(labelWhoseTurn, BoardWidth / 2 - label.getWidth() / 2, 60);
		}
	}

	private void removeAIMenu() {
		remove(labelMenu);
		remove(crossSample);
		remove(nullSample);
		remove(menuBoard);
		remove(EasyAI);
		remove(ImpossibleAI);
		remove(boxChoicePos);
		remove(boxChoiceImpos);

	}
	/**
	 * for playing again after winning screen is displayed
	 */
	public void keyPressed(KeyEvent e) {
		if(!mouseListenerIsActive) {
			this.removeAll();
			mouseListenerIsActive = true;
			placementForAI = new byte[]{0,0,0,0,0,0,0,0,0};
			mouseClickedForBegin = false;
			hasPlayerWon = false;
			hasAIWon = false;
			isDraw = false;
		}
	}
	/**
	 * sets up winning screen
	 */
	private void addWinScreen() {
		if(!isPLayingVSAI) remove(labelWhoseTurn);
		GRect backGrWin = new GRect(BoardWidth / 6, BoardHeight / 4, BoardWidth / 3 * 2, BoardHeight / 2);
		backGrWin.setFilled(true);
		backGrWin.setFillColor(Color.WHITE);
		add(backGrWin);
		String labelText;
		GImage imgFinale = new GImage("");
		if(isDraw) {
			labelText = "Nobody is a winner! ";
			imgFinale = new GImage("drawscreen.png");

		}
		else {
			if(!isPLayingVSAI){
				labelText = (isPlayingCrosses) ? "Crosses won!" : "Noughts won!";
			}
			else {
				if ((isPlayingCrosses && !hasAIWon) || (!isPlayingCrosses && hasAIWon)) labelText = "Crosses won!";
				else labelText = "Noughts won!";
			}
			if(!isPLayingVSAI)
				imgFinale = hasFirstPlayerWon ? new GImage("FirstPlayerWon.png") : new GImage("SecondPlayerWon.png");
			else {
				if (hasPlayerWon) imgFinale = new GImage("winscreen.png");
				else if (hasAIWon) imgFinale = new GImage("losescreen.png");
			}
		}

		GLabel labelFinale = new GLabel(labelText);
		GLabel labelFinale2 = new GLabel("Press any key to play again");

		labelFinale.setFont("Calibri-30");
		labelFinale2.setFont("Calibri-30");
		add(labelFinale, BoardWidth / 2 - labelFinale.getWidth() / 2, BoardHeight / 4 + labelFinale.getHeight());
		add(labelFinale2, BoardWidth / 2 - labelFinale2.getWidth() / 2, BoardHeight / 4 + labelFinale.getHeight() + labelFinale2.getHeight());
		imgFinale.setSize(BoardWidth / 3 * 2, BoardHeight / 2);
		add(imgFinale, BoardWidth / 6, BoardHeight /3  + labelFinale.getHeight() + 10 );

	}
	/**
	 * sets up board for the game
	 */
	private void setupBoard() {
		backGround = new GRect(0,BoardHeight / 8,BoardWidth, BoardHeight * 7 / 8 );
		backGround.setFilled(true);
		backGround.setColor(Color.WHITE);
		add(backGround);
		GLine lineBorder = new GLine(0, BoardHeight / 8, BoardWidth, BoardHeight / 8);
		add(lineBorder);
		for(int i = 1; i < 5; i++) {
			GRect lineB;
			if(i < 3) {
				lineB = new GRect(0, BoardHeight / 8 + cellHeight * i + LineWidth * (i -1), BoardWidth, LineWidth);

			}
			else {
				lineB = new GRect(cellWidth * (i -2) + LineWidth * (i - 3), BoardHeight / 8, LineWidth, BoardHeight / 8 * 7);
			}
			lineB.setFilled(true);
			lineB.setColor(Color.BLACK);
			add(lineB);
		}
	}
	/**
	 * sets up menu before the game
	 */
	private void setupMenu(){
		isAIModEasy = false;
		hasFirstPlayerWon = false;
		setupBoard();
		menuBoard = new GRect(BoardWidth / 6, BoardHeight / 4, BoardWidth / 3 * 2,BoardHeight / 2);
		menuBoard.setFilled(true);
		menuBoard.setFillColor(Color.WHITE);
		add(menuBoard);
		labelMenu = new GLabel("Choose number of players: ");
		labelMenu.setFont("Calibri-28");
		add(labelMenu, BoardWidth / 2 - labelMenu.getWidth() / 2, BoardHeight / 3);
		OnePlayer = new GLabel("Single Player");
		OnePlayer.setFont("Calibri-28");
		add(OnePlayer,BoardWidth / 3,BoardHeight / 5*2 );
		TwoPlayers = new GLabel("Two players");
		TwoPlayers.setFont("Calibri-28");
		add(TwoPlayers,BoardWidth / 2 + Math.abs(BoardWidth /6 - OnePlayer.getWidth()),BoardHeight / 5 * 2 );
	}

	public void mouseClicked(MouseEvent e) {
		if (!mouseListenerIsActive) return;
		GObject clickedObj = getElementAt(e.getX(), e.getY());
		if(clickedObj == OnePlayer || clickedObj == TwoPlayers){
			remove(labelMenu);
			remove(OnePlayer);
			remove(TwoPlayers);
			if(clickedObj == OnePlayer){
				isPLayingVSAI = true;
				setupMenuVSAI();

			}
			else{
				isPLayingVSAI = false;
				remove(menuBoard);
				mouseClickedForBegin = true;
				isPlayingCrosses = true;
			}
		}

		if(clickedObj == crossSample) {
			isPlayingCrosses = true;
			mouseClickedForBegin = true;
		}
		else if(clickedObj == nullSample) {
			isPlayingCrosses = false;
			mouseClickedForBegin = true;
		}
		if(clickedObj == EasyAI){
			isAIModEasy = true;
			boxChoicePos.setColor(Color.YELLOW);
			boxChoiceImpos.setColor(Color.WHITE);
		}
		else if(clickedObj == ImpossibleAI){
			isAIModEasy = false;
			boxChoiceImpos.setColor(Color.YELLOW);
			boxChoicePos.setColor(Color.WHITE);
		}
		if(!mouseClickedForBegin) return;
		if(clickedObj == backGround) {
			int mouseX = e.getX();
			int mouseY = e.getY();
			addCrossOrNull(mouseX, mouseY);
		}

	}

	private void setupMenuVSAI() {
		crossSample = new GImage("hrest.png");
		crossSample.scale(0.3);
		add(crossSample, BoardWidth / 4, BoardHeight / 5 * 2);
		nullSample = new GImage("null.png");
		nullSample.scale(0.3);
		add(nullSample, BoardWidth / 2, BoardHeight / 5 * 2 - 20);
		EasyAI = new GLabel("Possible");
		EasyAI.setFont("Calibri-28");
		ImpossibleAI = new GLabel("Impossible");
		ImpossibleAI.setFont("Calibri-28");

		add(EasyAI, BoardWidth / 2 - (EasyAI.getWidth() + ImpossibleAI.getWidth() + 5) / 2, BoardHeight / 3 * 2);
		add(ImpossibleAI, EasyAI.getWidth() + EasyAI.getX() + 5, BoardHeight / 3 * 2);
		boxChoicePos = new GRect(EasyAI.getX(), BoardHeight / 3 *2 - EasyAI.getHeight(), EasyAI.getWidth() + 1, EasyAI.getHeight() + EasyAI.getAscent() );
		boxChoicePos.setFilled(true);
		boxChoicePos.setColor(Color.YELLOW);
		add(boxChoicePos);
		boxChoiceImpos = new GRect(EasyAI.getX()+ EasyAI.getWidth() + 3, boxChoicePos.getY(), ImpossibleAI.getWidth() + 1, ImpossibleAI.getHeight() + ImpossibleAI.getAscent() );
		boxChoiceImpos.setFilled(true);
		boxChoiceImpos.setColor(Color.WHITE);
		add(boxChoiceImpos);
		isAIModEasy = true;
		EasyAI.sendToFront();
		ImpossibleAI.sendToFront();
		labelMenu = new GLabel("Choose who do you want to play as: ");
		labelMenu.setFont("Calibri-28");
		add(labelMenu, BoardWidth / 2 - labelMenu.getWidth() / 2, BoardHeight / 3);
	}

	/**
	 * adds cross or nought depending on isPlayingCrosses,mouseX,mouseY
	 * @param mouseX X position of the mouse
	 * @param mouseY Y position
	 */
	private void addCrossOrNull(int mouseX, int mouseY) {
		GImage img;
		int place = mouseX / (BoardWidth / 3) + (mouseY - BoardHeight / 8) / (BoardHeight / 8 * 7 / 3) * 3;
		if(isPLayingVSAI) changePlacementForAI(place, (byte) 1);
		if(isPlayingCrosses) {
			img = new GImage("hrest.png", place % 3 * (cellWidth + LineWidth), place / 3 * (cellHeight + LineWidth) + BoardHeight / 8);
			if(!isPLayingVSAI){
				changePlacementForAI(place, (byte) 1);
				labelWhoseTurn.setLabel("It`s 2nd player`s turn");
			}

		}
		else {
			img = new GImage("null.png", place % 3 * (cellWidth + LineWidth), place / 3 * (cellHeight + LineWidth) + BoardHeight / 8);
			if(!isPLayingVSAI){
				changePlacementForAI(place, (byte) 2);
				labelWhoseTurn.setLabel("It`s 1st player`s turn");
			}
		}

		img.setSize(cellWidth, cellHeight);
		add(img);
		if(checkForWin()) {
			hasPlayerWon = true;
			if(!isPLayingVSAI){
				hasFirstPlayerWon = isPlayingCrosses;
			}
			return;
		}
		if(!isPLayingVSAI)isPlayingCrosses = !isPlayingCrosses;
		else{
			AIPlays();
			if(checkForWin()) {
				hasAIWon = true;
			}
		}


	}
	/**
	 * plays for computer
	 */
	private void AIPlays() {
		for(int i =0; i < 9; i++) {
			if(placementForAI[i] == 0) {
				changePlacementForAI(i, (byte) 2);
				if(checkForWin()) {
					AIAddIMG(i);
					return;
				}
				else changePlacementForAI(i, (byte)0);
			}
		}
		for(int i = 0; i < 9; i++) {
			if(placementForAI[i] == 0) {
				changePlacementForAI(i, (byte)1);
				if(checkForWin()) {
					AIAddIMG(i);
					changePlacementForAI(i, (byte)2);
					return;
				}
				else changePlacementForAI(i, (byte)0);
			}
		}
		if(isAIModEasy){
			int curPlace = 0;
			while(placementForAI[curPlace] != 0) curPlace++;
			changePlacementForAI(curPlace, (byte)2);
			AIAddIMG(curPlace);
			return;
		}
		if(isPlayingCrosses && placementForAI[4] == 2 && (placementForAI[0] + placementForAI[2] + placementForAI[6] + placementForAI[8] == 2) && sum(placementForAI) == 4){
			changePlacementForAI(1, (byte) 2);
			AIAddIMG(1);
			return;
		}
		for(int i = 0; i < 9; i++){
			if(placementForAI[i] == 0){
				changePlacementForAI(i, (byte) 2);
				if(checkForFork(2)){
					AIAddIMG(i);
					return;
				}
				changePlacementForAI(i, (byte) 0);
			}


		}
		for(int i = 0; i < 9; i++){
			if(placementForAI[i] == 0){
				changePlacementForAI(i, (byte) 1);
				if(checkForFork(1)){
					changePlacementForAI(i, (byte) 2);
					AIAddIMG(i);
					return;
				}
				changePlacementForAI(i, (byte) 0);
			}


		}
		if(placementForAI[4] == 0){
			placementForAI[4] =2;
			AIAddIMG(4);
			return;
		}
		for(int i = 0; i < 9; i+= 2){
			if(placementForAI[i] == 0){
				AIAddIMG(i);
				placementForAI[i] = 2;
				return;
			}
		}
		for(int i = 1; i < 9; i+= 2){
			if(placementForAI[i] == 0){
				AIAddIMG(i);
				placementForAI[i] = 2;
				return;
			}
		}




	}

	private byte sum(byte[] placementForAI) {
		byte sum = 0;
		for (byte a:
			 placementForAI) {
			sum += a;
		}
		return sum;
	}

	private boolean checkForFork(int b) {
		int countLinesWithTwoBs = 0;
		int countBs = 0;
		for(int i = 0; i < 3; i++){
			for(int j = i; j < 9; j+=3){
				if(placementForAI[j] == b) countBs++;
				else if(placementForAI[j] != 0) countBs--;
			}
			if(countBs == 2) countLinesWithTwoBs++;
			countBs = 0;
			for(int j = i*3; j < i*3 + 3; j++) {
				if (placementForAI[j] == b) countBs++;
				else if(placementForAI[j] != 0) countBs--;
			}
			if(countBs == 2) countLinesWithTwoBs++;
			countBs = 0;

		}
		for (int i = 0; i < 9; i+= 4){
			if(placementForAI[i] == b) countBs++;
			else if(placementForAI[i] != 0) countBs--;
		}
		if(countBs == 2) countLinesWithTwoBs++;
		countBs = 0;
		for (int i = 2; i < 7; i+= 2){
			if(placementForAI[i] == b) countBs++;
			else if(placementForAI[i] != 0) countBs--;
		}
		if(countBs == 2) countLinesWithTwoBs++;
		return countLinesWithTwoBs > 1;
	}

	/**
	 * checks if is zero twos in placementForAI
	 */
	private boolean zeroTwos() {
		for(int i = 0; i < 9; i++) {
			if(placementForAI[i] == 2) return false;
		}
		return true;
	}

	/**
	 * Sets apropriate image when AI plays
	 * @param place where it should be set
	 */
	private void AIAddIMG(int place) {
		GImage img;
		if(!isPlayingCrosses) {
			img = new GImage("hrest.png", place % 3 * (cellWidth + LineWidth), place / 3 * (cellHeight + LineWidth) + BoardHeight / 8);
		}
		else img = new GImage("null.png", place % 3 * (cellWidth + LineWidth), place / 3 * (cellHeight + LineWidth) + BoardHeight / 8);
		img.setSize(cellWidth, cellHeight);
		add(img);

	}
	/**
	 * check if the game is over and sets isDraw if game ends
	 * @return true if it is
	 */
	private boolean checkForWin() {
		for(int i = 0; i<3; i++) {
			if(placementForAI[i] == placementForAI[i+3] && placementForAI[i+3] == placementForAI[i + 6] && placementForAI[i] != 0) return true;
			if(placementForAI[i * 3] == placementForAI[i * 3+1] && placementForAI[i * 3+1] == placementForAI[i* 3 + 2] && placementForAI[i*3] != 0 ) return true;
		}
		if(placementForAI[0] == placementForAI[4] && placementForAI[4] == placementForAI[8] && placementForAI[0] != 0) return true;
		if(placementForAI[2] == placementForAI[4] && placementForAI[4] == placementForAI[6] && placementForAI[2] != 0) return true;
		if(zeroZeroes()) {
			isDraw = true;
			return true;
		}
		return false;
	}
	/**
	 * checks if number of 0 in placementForAI is 0
	 * @return
	 */
	private boolean zeroZeroes() {
		for(int i = 0; i < 9; i++) {
			if(placementForAI[i] == 0) return false;
		}
		return true;
	}
	/**
	 * Changes string placementforAI
	 * @param place number of char in string
	 * @param charChange for what to change
	 */
	private void changePlacementForAI(int place,byte charChange) {
		if(place <= 0) {
			placementForAI[0] = charChange;
			return;
		}
		if(place >= 8) {
			placementForAI[8] = charChange;
			return;
		}
		placementForAI[place] =  charChange;

	}
	private byte[] placementForAI = new byte[]{0,0,0,0,0,0,0,0,0}; //0 - no one placed, 1-player placed, 2- AI placed
	private boolean mouseClickedForBegin = false;
	private boolean isPlayingCrosses;
	private boolean hasPlayerWon = false;
	private boolean hasAIWon = false;
	private boolean isDraw = false;
	private boolean mouseListenerIsActive = true;
	private GImage crossSample;
	private GImage nullSample;
	private GRect backGround;
	private GRect menuBoard;
	private GLabel OnePlayer;
	private GLabel TwoPlayers;
	private GLabel labelMenu;
	private GLabel labelWhoseTurn;
	private GLabel EasyAI;
	private GLabel ImpossibleAI;
	private GRect boxChoicePos;
	private GRect boxChoiceImpos;
	private boolean isAIModEasy;
	private boolean isPLayingVSAI;
	private boolean hasFirstPlayerWon;
}
