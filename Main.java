/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
public class Main {

	public static void main (String args[]) {	
		Monopoly monopoly = new Monopoly();		
		monopoly.inputNames();
		monopoly.giveStartMoney();
		monopoly.decideStarter();
		do {
			monopoly.processTurn();
			if (!monopoly.isGameOver()) {
				monopoly.nextPlayer();
			}
		} while (!monopoly.isGameOver());
		monopoly.decideWinner();
		monopoly.displayGameOver();
		return;
	}
	
}
