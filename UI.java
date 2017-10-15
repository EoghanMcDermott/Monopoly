/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
import java.awt.BorderLayout;

import javax.swing.JFrame;

import java.util.ArrayList;

public class UI {

	private static final int FRAME_WIDTH = 1200;
	private static final int FRAME_HEIGHT = 800;
	private static final String CURRENCY = "£";
	
	public static final int CMD_QUIT = 0;
	public static final int CMD_DONE = 1;
	public static final int CMD_ROLL = 2;
	public static final int CMD_BUY = 3;
	public static final int CMD_PROPERTY = 4;
	public static final int CMD_BALANCE = 5;
	public static final int CMD_BANKRUPT = 6;
	public static final int CMD_HELP = 7;
	public static final int CMD_MORTGAGE = 8;
	public static final int CMD_BUILD = 9;
	public static final int CMD_REDEEM = 10;
	public static final int CMD_CHEAT = 11;
	public static final int CMD_DEMOLISH = 12;
	public static final int CMD_JAIL_PAY = 13;//added new jail commands
	public static final int CMD_JAIL_CARD = 14;
	
	public static final int ERR_SYNTAX = 0;
	public static final int ERR_DOUBLE_ROLL = 1;
	public static final int ERR_NO_ROLL = 2;
	public static final int ERR_INSUFFICIENT_FUNDS = 3;
	public static final int ERR_NOT_A_PROPERTY = 4;
	public static final int ERR_RENT_ALREADY_PAID = 5;
	public static final int ERR_NOT_OWNED = 6;
	public static final int ERR_IS_OWNED = 7;
	public static final int ERR_SELF_OWNED = 8;
	public static final int ERR_RENT_OWED= 9;
	public static final int ERR_NOT_A_NAME = 10;
	public static final int ERR_TOO_MANY_BUILDINGS = 11;
	public static final int ERR_NOT_A_SITE = 12;
	public static final int ERR_NOT_YOURS = 13;
	public static final int ERR_TOO_FEW_BUILDINGS = 14;
	public static final int ERR_DOES_NOT_HAVE_GROUP = 15;
	public static final int ERR_DUPLICATE = 16;
	public static final int ERR_HAS_BUILDINGS = 17;
	public static final int ERR_IS_MORTGAGED = 18;
	public static final int ERR_IS_NOT_MORTGAGED = 19;
	public static final int SITE_IS_MORTGAGED = 20;
	public static final int ERR_NO_JAIL_CARD = 21;
	
	private final String[] errorMessages = {
		"Error: Not a valid command.",
		"Error: Too many rolls this turn.",
		"Error: You have a roll to play.",
		"Error: You don't have enough money.",
		"Error: This square is not a property.",
		"Error: You have already paid the rent.",
		"Error: The property is not owned.",
		"Error: The property is already owned.",
		"Error: You own the property.",
		"Error: You owe rent.",
		"Error: Not a name.",
		"Error: Too many units.",
		"Error: That property is not a site.",
		"Error: You do not own that property.",
		"Error: Must be one or more units",
		"Error: You do not own the whole colour group.",
		"Error: Duplicate name.",
		"Error: The site has units on it.",
		"Error: The property has already been mortgaged.",
		"Error: The property has not been mortgaged.",
		"Error: The property has been mortgaged.",
		"Error: You do not have a get out of jail free card"
	};
	
	private JFrame frame = new JFrame();
	private BoardPanel boardPanel;	
	private InfoPanel infoPanel = new InfoPanel();
	private CommandPanel commandPanel = new CommandPanel();
	private String string;
	private boolean done;
	private int commandId;
	private Board board;
	private Players players;
	private Property inputProperty;
	private int inputNumber;
	private Player inputPlayer;

	UI (Players players, Board board) {
		this.players = players;
		this.board = board;
		boardPanel = new BoardPanel(this.players);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setTitle("Monopoly");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(boardPanel, BorderLayout.LINE_START);
		frame.add(infoPanel, BorderLayout.LINE_END);
		frame.add(commandPanel,BorderLayout.PAGE_END);
		frame.setResizable(false);
		frame.setVisible(true);
		return;
	}

//  METHODS DEALING WITH USER INPUT
	
	public void inputName (int numPlayers) {
		boolean inputValid = false;
		if (numPlayers == 0) {
			infoPanel.displayString("Enter new player name (" + boardPanel.getTokenName(numPlayers) + "):");			
		} else {
			infoPanel.displayString("Enter new player name (" + boardPanel.getTokenName(numPlayers)  +  ") or done:");
		}
		do {
			commandPanel.inputString();
			string = commandPanel.getString();
			string = string.trim();
			if (string.length()==0) {
				inputValid = false;
				done = false;
			} else if ( (numPlayers > 0) && (string.toLowerCase().equals("done")) ) {
				inputValid = true;
				done = true;
			} else if (string.contains(" ")) {
				inputValid = false;
				done = false;
			} else {
				inputValid = true;
			}
			infoPanel.displayString("> " + string);
			if (!inputValid) {
				displayError(ERR_NOT_A_NAME);
			}
		} while (!inputValid);
		return;
	}
	
	private boolean hasNoArgument (String[] words) {
		return (words.length == 1);
	}
	
	private boolean hasOneArgument (String[] words) {
		return (words.length == 2);
	}	

	private boolean hasTwoArguments (String[] words) {
		return (words.length==3);
	}
	
	public void inputCommand (Player player) {
		boolean inputValid = false;
		do {
			infoPanel.displayString(player + " type your command:");
			commandPanel.inputString();
			string = commandPanel.getString();
			infoPanel.displayString("> " + string);
			string = commandPanel.getString();
			string = string.toLowerCase();
			string = string.trim();
			string = string.replaceAll("( )+", " ");
			String[] words = string.split(" ");
			switch (words[0]) {
				case "quit" :
					commandId = CMD_QUIT;
					inputValid = hasNoArgument(words);
					break;
				case "done" :
					commandId = CMD_DONE;
					inputValid = hasNoArgument(words);
					break;
				case "roll" :
					commandId = CMD_ROLL;
					inputValid = hasNoArgument(words);
					break;
				case "buy" :
					commandId = CMD_BUY;
					inputValid = hasNoArgument(words);
					break;
				case "property" :
					commandId = CMD_PROPERTY;
					inputValid = hasNoArgument(words);
					break;
				case "balance" :
					commandId = CMD_BALANCE;
					inputValid = hasNoArgument(words);
					break;
				case "bankrupt" :
					commandId = CMD_BANKRUPT;
					inputValid = hasNoArgument(words);
					break;
				case "mortgage" :
					commandId = CMD_MORTGAGE;
					if (hasOneArgument(words) && board.isProperty(words[1])) { 
						inputProperty = board.getProperty(words[1]);
						inputValid = true;
					} else {
						inputValid = false;
					}
					break;
				case "redeem" :
					commandId = CMD_REDEEM;
					if (hasOneArgument(words) && board.isProperty(words[1])) { 
						inputProperty = board.getProperty(words[1]);
						inputValid = true;
					} else {
						inputValid = false;
					}
					break;
				case "build" :
					commandId = CMD_BUILD;
					if (hasTwoArguments(words) && board.isSite(words[1]) && words[2].matches("[0-9]+")) { 
						inputProperty = board.getProperty(words[1]);
						inputNumber = Integer.parseInt(words[2]);
						inputValid = true;
					} else {
						inputValid = false;
					}
					break;
				case "demolish" :
					commandId = CMD_DEMOLISH;
					if (hasTwoArguments(words) && board.isSite(words[1]) && words[2].matches("[0-9]+")) { 
						inputProperty = board.getProperty(words[1]);
						inputNumber = Integer.parseInt(words[2]);
						inputValid = true;
					} else {
						inputValid = false;
					}
					break;					
				case "help" :
					commandId = CMD_HELP;
					inputValid = hasOneArgument(words);
					break;
				case "cheat" :
					commandId = CMD_CHEAT;
					if (hasOneArgument(words) && words[1].matches("[0-9]+")) {
						inputNumber = Integer.parseInt(words[1]);
						inputValid = true;
					} else {
						inputValid = false;
					}
					break;
				case "pay" ://making new jail commands valid
					commandId = CMD_JAIL_PAY;
					inputValid = true;
					break;
				case "card" :
					commandId = CMD_JAIL_CARD;
					inputValid = true;
					break;
				default:
					inputValid = false;
				}
			if (!inputValid) {
				displayError(ERR_SYNTAX);
			}
		} while (!inputValid);
		if (commandId == CMD_DONE) {
			done = true;
		} else {
			done = false;
		}		
		return;
	}
	
	public String getString () {
		return string; 
	}
	
	public String getTokenName (int tokenId) {
		return boardPanel.getTokenName(tokenId);
	}
	
	public int getCommandId () {
		return commandId;
	}
	
	public boolean isDone () {
		return done;
	}
	
	public Property getInputProperty () {
		return inputProperty;
	}
	
	public Player getInputPlayer () {
		return inputPlayer;
	}
	
	public int getInputNumber () {
		return inputNumber;
	}
	
	
// DISPLAY METHODS
	
	public void display () {
		boardPanel.refresh();
		return;
	}
	
	public void displayString (String string) {
		infoPanel.displayString(string);
		return;
	}
	
	public void displayBankTransaction (Player player) {
		if (player.getTransaction() >= 0) {
			infoPanel.displayString(player + " receives " + CURRENCY + player.getTransaction() + " from the bank.");
		} else {
			infoPanel.displayString(player + " pays " + CURRENCY + (-player.getTransaction()) + " to the bank.");			
		}
		return;
	}
	
	public void displayTransaction (Player fromPlayer, Player toPlayer) {
		infoPanel.displayString(fromPlayer + " pays " + CURRENCY + toPlayer.getTransaction() + " to " + toPlayer);
		return;
	}
	
	public void displayDice (Player player, Dice dice) {
		infoPanel.displayString(player + " rolls " + dice + ".");
		return;
	}
	
	public void displayRollDraw () {
		infoPanel.displayString("Draw");
		return;
	}
	
	public void displayRollWinner (Player player) {
		infoPanel.displayString(player + " wins the roll.");
		return;
	}
	
	public void displayGameOver () {
		infoPanel.displayString("GAME OVER");
		return;
	}
	
	public void displayCommandHelp () {
		infoPanel.displayString("Available commands: roll, buy, pay rent, build, demolish, mortgage, redeem, bankrupt, property, balance, done, quit. ");
		return;
	}
	
	public void displayBalance (Player player) {
		infoPanel.displayString(player + "'s balance is " + CURRENCY + player.getBalance());
		return;
	}
	
	public void displayError (int errorId) {
		infoPanel.displayString(errorMessages[errorId]);
		return;
	}
	
	public void displayPassedGo (Player player) {
		infoPanel.displayString(player + " passed Go.");
		return;
	}
	
	public void displayLatestProperty (Player player) {
		infoPanel.displayString(player + " bought " + player.getLatestProperty());
	}
	
	public void displayProperty (Player player) {
		ArrayList<Property> propertyList = player.getProperties();
		if (propertyList.size() == 0) {
			infoPanel.displayString(player + " owns no property.");
		} else {
			infoPanel.displayString(player + " owns the following property...");
			for (Property p : propertyList) {
				String mortgageStatus = "";
				if (p.isMortgaged()) {
					mortgageStatus = ", is mortgaged";
				}
				if (p instanceof Site) {
					Site site = (Site) p;
					String buildStatus = "";
					if (site.getNumBuildings()==0) {
						buildStatus = "with no buildings";
					} else if (site.getNumBuildings()==1) {
						buildStatus = "with 1 house";
					} else if (site.getNumBuildings()<5) {
						buildStatus = "with " + site.getNumBuildings() + " houses";
					} else if (site.getNumBuildings()==5) {
						buildStatus = "with a hotel";
					}
					infoPanel.displayString(site + " (" + site.getColourGroup().getName() + "), rent " + CURRENCY + site.getRent() +  ", " + buildStatus + mortgageStatus + ".");		
				} else if (p instanceof Station) {
					infoPanel.displayString(p + ", rent " + CURRENCY + p.getRent() +  mortgageStatus + ".");	
				} else if (p instanceof Utility) {
					infoPanel.displayString(p + ", rent " + ((Utility) p).getRentMultiplier() + " times dice" + mortgageStatus + ".");
				}
			}
		}
	}
	
	public void displaySquare (Player player, Board board, Dice dice) {
		Square square = board.getSquare(player.getPosition());
		infoPanel.displayString(player + " arrives at " + square.getName() + ".");
		if (square instanceof Property) {
			Property property = (Property) square;
			if (property.isOwned()) {
				infoPanel.displayString("The property is owned by " + property.getOwner() + ". Rent is " + CURRENCY + property.getRent() + ".");				
			} else {
				infoPanel.displayString("The property is not owned.");								
			}
		}
		return;
	}
	
	public void displayBuild (Player player, Site site, int numUnits) {
		if (numUnits==1) {
			infoPanel.displayString(player + " builds 1 unit on " + site);			
		} else {
			infoPanel.displayString(player + " builds " + numUnits + " units on " + site);
		}
		return;
	}
	
	public void displayDemolish (Player player, Site site, int numUnits) {
		if (numUnits==1) {
			infoPanel.displayString(player + " demolishes 1 unit on " + site);			
		} else {
			infoPanel.displayString(player + " demolishes " + numUnits + " units on " + site);
		}
		return;
	}	
	
	public void displayBankrupt (Player player) {
		infoPanel.displayString(player + " is bankrupt.");
		return;
	}
	
	public void displayMortgage (Player player, Property property) {
		infoPanel.displayString(player + " mortgages " + property + " for "+ CURRENCY + property.getMortgageValue());
		return;				
	}
	
	public void displayMortgageRedemption (Player player, Property property) {
		infoPanel.displayString(player + " redeems " + property + " for " + CURRENCY + property.getMortgageRemptionPrice());
		return;
	}
	
	public void displayAssets (Player player) {
		infoPanel.displayString(player + " has assets of " + CURRENCY + player.getAssets());
		return;
	}
	
	public void displayWinner (Player player) {
		infoPanel.displayString("The winner is " + player);
		return;
	}
	
	public void displayDraw (ArrayList<Player> players) {
		infoPanel.displayString("The following players drew the game " + players);
		return;
	}
	//jail info methods
	public void displayJailCommands(){
		infoPanel.displayString("You are in jail.\n" + 
								"Enter \"pay\" to pay " + CURRENCY +"50 to get out.\n" +
								"Enter \"roll\" to try and roll doubles to get out.\n" + 
								"Enter \"card\"  to use a get out of jail free card if you have one.");
	}
	
	public void displayOutOfJail(){
		infoPanel.displayString("You are now out of jail, congratulations.");
	}
}
