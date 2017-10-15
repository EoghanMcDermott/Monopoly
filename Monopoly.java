/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
import java.util.ArrayList;


public class Monopoly {

	private static final int START_MONEY = 1500;
	private static final int GO_MONEY = 200;
	private static final int INCOME_TAX = 4;
	private static final int SUPER_TAX = 38;
	private static final int[] COMMUNITY_SQUARES = {2,17,33};
	private static final int[] CHANCE_SQUARES = {7,22,36};
	private static final int GO_TO_JAIL = 30;
	private static final int JAIL_TIME = 3;//jail time is 3 turns
	
	private Players players = new Players();
	private Player currPlayer;
	private Dice dice = new Dice();
	private Board board = new Board(dice);
	private UI ui = new UI(players, board);
	private boolean gameOver = false;
	private boolean onlyOneNotBankrupt = false;
	private boolean turnFinished;
	private boolean rollDone;
	
	private Chance chance = new Chance();
	private CommunityChest community = new CommunityChest();
	
	
	Monopoly () {
		ui.display();
		return;
	}
		
	public void inputNames () {
		int playerId = 0;
		do {
			ui.inputName(playerId);
			if (!ui.isDone()) {
				boolean duplicate = false;
				for (Player p : players.get()) {
					if (ui.getString().toLowerCase().equals(p.getName().toLowerCase())) {
						duplicate = true;
					}
				}
				if (!duplicate) {
					players.add(new Player(ui.getString(),ui.getTokenName(playerId),playerId));
					playerId++;
				} else {
					ui.displayError(UI.ERR_DUPLICATE);
				}
			}
		} while (!ui.isDone() && players.canAddPlayer());
		return;
	}
	
	public void giveStartMoney () {
		for (Player p : players.get()) {
			p.doTransaction (START_MONEY);
			ui.displayBankTransaction (p);
		}
		return;
	}
	
	public void decideStarter () {
		Players inPlayers = new Players(players), selectedPlayers = new Players();
		boolean tie = false;
		do {
			int highestTotal = 0;
			for (Player p : inPlayers.get()) {
				dice.roll();
				ui.displayDice(p,dice);
				if (dice.getTotal() > highestTotal) {
					tie = false;
					highestTotal = dice.getTotal();
					selectedPlayers.clear();
					selectedPlayers.add(p);
				} else if (dice.getTotal() == highestTotal) {
					tie = true;
					selectedPlayers.add(p);
				}
			}
			if (tie) {
				ui.displayRollDraw();
				inPlayers = new Players(selectedPlayers);
				selectedPlayers.clear();
			}
		} while (tie);
		currPlayer = selectedPlayers.get(0);
		ui.displayRollWinner(currPlayer);
		ui.display();
		return;
	}
	
	private void processRoll () {
		int maxDoubles = 0;//if this gets to 3 then player goes to jail
		if (!rollDone) {
				dice.roll();
				ui.displayDice(currPlayer, dice);
				currPlayer.move(dice.getTotal());
				ui.display();
				if (currPlayer.passedGo()) {
					currPlayer.doTransaction(+GO_MONEY);
					ui.displayPassedGo(currPlayer);
					ui.displayBankTransaction(currPlayer);
				}
				ui.displaySquare(currPlayer, board, dice);
				if (board.getSquare(currPlayer.getPosition()) instanceof Property && 
						((Property) board.getSquare(currPlayer.getPosition())).isOwned() &&
						!((Property) board.getSquare(currPlayer.getPosition())).getOwner().equals(currPlayer) ) {
					
					Property property = (Property) board.getSquare(currPlayer.getPosition());
					if (property.isOwned()) {
						if (!property.getOwner().equals(currPlayer)) {
								int rent = property.getRent();
								if (currPlayer.getBalance()>=rent) {
									Player owner = property.getOwner();
									currPlayer.doTransaction(-rent);
									owner.doTransaction(+rent);
									ui.displayTransaction(currPlayer, owner);																				
							}
						}
					}					
				}
				
				if(currPlayer.getBalance()<=0){  //checks if the user has a negative balance 
					ui.displayError(UI.ERR_INSUFFICIENT_FUNDS);
					
					int i=0;
					do{ //do while loop to keep mortgaging the player's properties until their balance goes above zero
						if(currPlayer.numberOfProperties() == 0){ //checks if the user has any properties to mortgage
							break;
						}
					Property property = currPlayer.getProperties().get(i);   
					if (property.isOwned() && property.getOwner().equals(currPlayer)) {
						if ((property instanceof Site) && !((Site) property).hasBuildings() || (property instanceof Station) || (property instanceof Utility)) {
							if (!property.isMortgaged()) {
								property.setMortgaged();
								currPlayer.doTransaction(+property.getMortgageValue());
								ui.displayMortgage(currPlayer,property);
							}
						}	
					}		
					i++;
					if(i>currPlayer.numberOfProperties()-1){ //if the player runs out of properties to mortgage, the loop breaks
						break;
					}
				}while(currPlayer.getBalance()<=0);			
			}		 
					
				if(currPlayer.getBalance()<=0){ //if they still have a negative balance after attempts to mortgage their properties, they declare bankruptcy
						processBankrupt();
					}
				
				if (!dice.isDouble()) {
					rollDone = true;
				}

		} else {
			ui.displayError(UI.ERR_DOUBLE_ROLL);
		}
		
		if(dice.isDouble()){
			maxDoubles = maxDoubles + 1;
			if(maxDoubles > 2){
				currPlayer.goJail();
				//jail.goToJail(currPlayer);
				ui.displayString(currPlayer.getName() + " has rolled 3 doubles and must go directly to jail.");
			}
		}
		//TAX CHECKS
		if(currPlayer.getPosition() == INCOME_TAX){
			currPlayer.doTransaction(-200);
			ui.displayString(currPlayer.getName() +" pays income tax of 200.");
		}
		if(currPlayer.getPosition() == SUPER_TAX){
			currPlayer.doTransaction(-100);
			ui.displayString(currPlayer.getName() +" pays super tax of 100.");
		}
		
		//COMMUNITY CHEST CHECK
		if(currPlayer.getPosition() == COMMUNITY_SQUARES[0] || currPlayer.getPosition() == COMMUNITY_SQUARES[1] || currPlayer.getPosition() == COMMUNITY_SQUARES[2]){
			community.drawCommunity(currPlayer,players);
			ui.displayString(community.getMessage());
			ui.display();//refresh display in case token is moved
		}
		//CHANCE CHECK
		if(currPlayer.getPosition() == CHANCE_SQUARES[0] || currPlayer.getPosition() == CHANCE_SQUARES[1] || currPlayer.getPosition() == CHANCE_SQUARES[2]){
			chance.drawChance(currPlayer);
			ui.displayString(chance.getMessage());
			ui.display();//refresh display in case token is moved
		}
		
		//JAIL CHECK
		if(currPlayer.getPosition() == GO_TO_JAIL){
			currPlayer.goJail();//the player is now in jail
			ui.displayString("You have been sent to jail, enter \"done\" to finish your turn.");
			ui.display();//refreshing to see token in jail
		}
		
		return;
	}

	private void processBuy () {
		if (board.getSquare(currPlayer.getPosition()) instanceof Property) {
			Property property = (Property) board.getSquare(currPlayer.getPosition());
			if (!property.isOwned()) {
				if (currPlayer.getBalance() >= property.getPrice()) {				
					currPlayer.doTransaction(-property.getPrice());
					ui.displayBankTransaction(currPlayer);
					currPlayer.addProperty(property);
					ui.displayLatestProperty(currPlayer);
				} else {
					ui.displayError(UI.ERR_INSUFFICIENT_FUNDS);
				}
			} else {
				ui.displayError(UI.ERR_IS_OWNED);
			}
		} else {
			ui.displayError(UI.ERR_NOT_A_PROPERTY);
		}
		return;
	}
	
	private void processBuild () {
		Property property = ui.getInputProperty();
		if (property.isOwned() && property.getOwner().equals(currPlayer)) {
			if (property instanceof Site) {
				Site site = (Site) property;
				if (currPlayer.isGroupOwner(site)) {
					if (!site.isMortgaged()) {
						int numBuildings = ui.getInputNumber();
						if (numBuildings>0) {
							if (site.canBuild(numBuildings)) {
								int debit = numBuildings*site.getBuildingPrice();
								if (currPlayer.getBalance()>debit) {
									site.build(numBuildings);
									currPlayer.doTransaction(-debit);
									ui.displayBuild(currPlayer,site,numBuildings);
								} else {
									ui.displayError(UI.ERR_INSUFFICIENT_FUNDS);
								}
							} else {
								ui.displayError(UI.ERR_TOO_MANY_BUILDINGS);
							}
						} else {
							ui.displayError(UI.ERR_TOO_FEW_BUILDINGS);
						}
					} else {
						ui.displayError(UI.SITE_IS_MORTGAGED);
					}
				} else {
					ui.displayError(UI.ERR_DOES_NOT_HAVE_GROUP);
				}
			} else {
				ui.displayError(UI.ERR_NOT_A_SITE);
			}
		} else {
			ui.displayError(UI.ERR_NOT_YOURS);
		}
		return;
	}
	
	private void processDemolish () {
		Property property = ui.getInputProperty();
		if (property.isOwned() && property.getOwner().equals(currPlayer)) {
			if (property instanceof Site) {
				Site site = (Site) property;
				int numBuildings = ui.getInputNumber();
				if (numBuildings>0) {
					if (site.canDemolish(numBuildings)) {
						site.demolish(numBuildings);
						int credit = numBuildings * site.getBuildingPrice()/2;
						currPlayer.doTransaction(+credit);
						ui.displayDemolish(currPlayer,site,numBuildings);
					} else {
						ui.displayError(UI.ERR_TOO_MANY_BUILDINGS);
					}
				} else {
					ui.displayError(UI.ERR_TOO_FEW_BUILDINGS);
				}
			} else {
				ui.displayError(UI.ERR_NOT_A_SITE);
			}
		} else {
			ui.displayError(UI.ERR_NOT_YOURS);
		}
		return;		
	}
	
	public void processCheat () {
		switch (ui.getInputNumber()) {
			case 1 :       // acquire colour group
				Property property = board.getProperty("kent");
				currPlayer.addProperty(property);		
				property = board.getProperty("whitechapel");
				currPlayer.addProperty(property);
				break;
			case 2 :	   // make zero balance
				currPlayer.doTransaction(-currPlayer.getBalance());
				currPlayer.doTransaction(-200);
				break;
		}
		return;
	}
	
	public void processBankrupt () {
		ui.displayBankrupt(currPlayer);
		Player tempPlayer = players.getNextPlayer(currPlayer);
		players.remove(currPlayer);
		currPlayer = tempPlayer;
		if (players.numPlayers()==1) {
			gameOver = true;
			onlyOneNotBankrupt = true;
		}
		ui.display();
		return;
	}
	
	public void processMortgage () {
		Property property = ui.getInputProperty();
		if (property.isOwned() && property.getOwner().equals(currPlayer)) {
			if ((property instanceof Site) && !((Site) property).hasBuildings() || (property instanceof Station) || (property instanceof Utility)) {
				if (!property.isMortgaged()) {
					property.setMortgaged();
					currPlayer.doTransaction(+property.getMortgageValue());
					ui.displayMortgage(currPlayer,property);
				} else {
					ui.displayError(UI.ERR_IS_MORTGAGED);
				}
			} else {
				ui.displayError(UI.ERR_HAS_BUILDINGS);
			}
		} else {
			ui.displayError(UI.ERR_NOT_YOURS);
		}
		return;		
	}
	
	public void processRedeem () {
		Property property = ui.getInputProperty();
		if (property.isOwned() && property.getOwner().equals(currPlayer)) {
			if (property.isMortgaged()) {
				int price = property.getMortgageRemptionPrice();
				if (currPlayer.getBalance() >= price) {
					property.setNotMortgaged();
					currPlayer.doTransaction(-price);
					ui.displayMortgageRedemption(currPlayer,property);
				} else {
					ui.displayError(UI.ERR_INSUFFICIENT_FUNDS);
				}
			} else {
				ui.displayError(UI.ERR_IS_NOT_MORTGAGED);
			}
		} else {
			ui.displayError(UI.ERR_NOT_YOURS);
		}
		return;			
	}

	private void processDone () {
		
		if(currPlayer.getBalance()<=0){  //checks if the user has a negative balance 
			ui.displayError(UI.ERR_INSUFFICIENT_FUNDS);
			
			int i=0;
			do{ //do while loop to keep mortgaging the player's properties until their balance goes above zero
				if(currPlayer.numberOfProperties() == 0){ //checks if the user has any properties to mortgage
					break;
				}
			Property property = currPlayer.getProperties().get(i);  
			if (property.isOwned() && property.getOwner().equals(currPlayer)) {
				if ((property instanceof Site) && !((Site) property).hasBuildings() || (property instanceof Station) || (property instanceof Utility)) {
					if (!property.isMortgaged()) {
						property.setMortgaged();
						currPlayer.doTransaction(+property.getMortgageValue());
						ui.displayMortgage(currPlayer,property);
					}
				}	
			}
			i++;
			if(i>currPlayer.numberOfProperties()-1 ){ //if the player runs out of properties to mortgage, the loop breaks
				break;
			}
		}while(currPlayer.getBalance()<=0);
		
	}
		if(currPlayer.getBalance()<=0){ //if they still have a negative balance after attempts to mortgage their properties, they declare bankruptcy
			processBankrupt();
		}
		
		if (dice.isDouble()){
			ui.displayError(UI.ERR_NO_ROLL);
			rollDone = false;
		}
			
		else if(currPlayer.getBalance()>0){
			rollDone = true;
		}
		
		if (rollDone) {
				turnFinished = true;
		}		
		
		return;
	}
	
	public void processJail(){
		boolean turnDone = false;
			
		if(currPlayer.getTime() < JAIL_TIME){//player should be in jail for at most 3 turns
			ui.displayString("You have been in jail for " + currPlayer.getTime() + " turns.");
			ui.displayJailCommands();//showing valid jail commands
			do{
				ui.inputCommand(currPlayer);
				switch(ui.getCommandId()){
				
					case UI.CMD_ROLL :
						dice.roll();
						ui.displayDice(currPlayer, dice);
						if(dice.isDouble()){
							currPlayer.escapeJail();
							ui.displayOutOfJail();
						}
						else{
							ui.displayString("Sorry, you didn't roll a double, your turn is over.");
							currPlayer.SpendTurnInJail();
						}
						turnDone = true;
						break;
						
					case UI.CMD_JAIL_PAY :
						currPlayer.doTransaction(-50);//paying 50 to get out of jail
						currPlayer.escapeJail();
						ui.displayOutOfJail();
						turnDone = true;
						break;
						
					case UI.CMD_JAIL_CARD :
						if(currPlayer.hasJailCard())
						{
							currPlayer.takeJailCard();
							currPlayer.escapeJail();
							ui.displayOutOfJail();
							turnDone = true;						}
						else
							ui.displayError(UI.ERR_NO_JAIL_CARD);//can't get use a card if you don't have one
						break;
					//some other commands should still be accepted - done & quit	

					}
			}while(!turnDone);
		}
		else{
			ui.displayOutOfJail();
			currPlayer.escapeJail();
		}
	}

		
	
	public void processTurn () {
		turnFinished = false;
		rollDone = false;
		
		if(currPlayer.inJail()){
						processJail();
						turnFinished = true;
		}
		else{
				do {
					ui.inputCommand(currPlayer);
					switch (ui.getCommandId()) {
						case UI.CMD_ROLL :
							processRoll();
							break;
						case UI.CMD_BUY :
							processBuy();
							break;
						case UI.CMD_BALANCE :
							ui.displayBalance(currPlayer);
							break;
						case UI.CMD_PROPERTY :
							ui.displayProperty(currPlayer);
							break;
						case UI.CMD_BANKRUPT :
							processBankrupt();
							turnFinished = true;
							break;
						case UI.CMD_BUILD :
							processBuild();
							break;
						case UI.CMD_DEMOLISH :
							processDemolish();
							break;
						case UI.CMD_REDEEM :
							processRedeem();
							break;
						case UI.CMD_MORTGAGE :
							processMortgage();
							break;
						case UI.CMD_CHEAT :
							processCheat();
							break;
						case UI.CMD_HELP :
							ui.displayCommandHelp();
							break;
						case UI.CMD_DONE :
							processDone();
							break;
						case UI.CMD_QUIT : 
							turnFinished = true;
							gameOver = true;
							break;
					}
				} while (!turnFinished);
		
				return;
		}
	}
	
	public void nextPlayer () {
		currPlayer = players.getNextPlayer(currPlayer);
		return;
	}
	
	public void decideWinner () {
		if (onlyOneNotBankrupt) {
			ui.displayWinner(currPlayer);			
		} else {
			ArrayList<Player> playersWithMostAssets = new ArrayList<Player>();
			int mostAssets = players.get(0).getAssets();
			for (Player player : players.get()) {
				ui.displayAssets(player);
				if (player.getAssets() > mostAssets) {
					playersWithMostAssets.clear(); 
					playersWithMostAssets.add(player);
				} else if (player.getAssets() == mostAssets) {
					playersWithMostAssets.add(player);
				}
			}
			if (playersWithMostAssets.size() == 1) {
				ui.displayWinner(playersWithMostAssets.get(0));
			} else {
				ui.displayDraw(playersWithMostAssets);
			}
		}
		return;
	}
	
	public void displayGameOver () {
		ui.displayGameOver ();
		return;
	}
	
	public boolean isGameOver () {
		return gameOver;
	}
}
