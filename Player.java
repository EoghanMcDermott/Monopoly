/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
import java.util.ArrayList;

public class Player {
	
	private final static int JAIL = 10;
	
	private String name;
	private int position;
	private int balance;
	private int amount;
	private String tokenName;
	private int tokenId;
	private boolean passedGo;
	private ArrayList<Property> properties = new ArrayList<Property>();
	private boolean inJail = false;//no player in jail at start
	private boolean jailCard = false;//no player has get out of jail card at start
	private int timeInJail = 0;
	private int number = 0;
	
// CONSTRUCTORS
	
	Player (String name, String tokenName, int tokenId) {
		this.name = name;
		this.tokenName = tokenName;
		this.tokenId = tokenId;
		position = 0;
		balance = 0;
		passedGo = false;
		return;
	}
	
	Player (Player player) {   // copy constructor
		this(player.getName(), player.getTokenName(), player.getTokenId());
	}
	
// METHODS DEALING WITH PLAYER DATA
	
	public String getName () {
		return name;
	}
	
	public String getTokenName () {
		return tokenName;
	}
	
	public int getTokenId () {
		return tokenId;
	}
	
// METHODS DEALING WITH TOKEN POSITION
	
	public int getPosition () {
		return position;
	}

	public void move (int squares) {
		position = position + squares;
		if (position >= Board.NUM_SQUARES) {
			position = position - Board.NUM_SQUARES;
			passedGo = true;
		} else {
			passedGo = false;
		}
		if (position < 0) {
			position = position + Board.NUM_SQUARES;
		} 
		return;
	}
	
	public void setPosition(int pos)
	{
		position = pos;
	}

	public boolean passedGo () {
		return passedGo;
	}
	
//  METHODS DEALING WITH MONEY
	
	public void doTransaction (int amount) {
		balance = balance + amount;
		this.amount = amount;
		return;
	}
		
	public int getTransaction () {
		return amount;
	}
	
	public int getBalance () {
		return balance;
	}
	
	public int getAssets () {
		int assets = balance;
		for (Property property: properties) {
			assets = assets + property.getPrice();
		}
		return assets;
	}
	
//  METHODS DEALING WITH PROPERTY
	
	public void addProperty (Property property) {
		property.setOwner(this);
		properties.add(property);
		number++;
		return;
	}
	
	public int numberOfProperties() { //method to return the number of properties a user owns
		return number;
	}
	public Property getLatestProperty () {
		return properties.get(properties.size()-1);
	}
	
	public ArrayList<Property> getProperties () {
		return properties;
	}
	
	public boolean isGroupOwner (Site site) {
		boolean ownsAll = true;
		ColourGroup colourGroup = site.getColourGroup();
		for (Site s : colourGroup.getMembers()) {
			if (!s.isOwned() || (s.isOwned() && s.getOwner() != this))
				ownsAll = false;
		}
		return ownsAll;
	}
	
	public int getNumStationsOwned () {
		int numOwned = 0;
		for (Property p : properties) {
			if (p instanceof Station) {
				numOwned++;
			}
		}
		return numOwned;
	}
	
	public int getNumUtilitiesOwned () {
		int numOwned = 0;
		for (Property p : properties) {
			if (p instanceof Utility) {
				numOwned++;
			}
		}
		return numOwned;
	}
	
// COMMON JAVA METHODS	
	
	public boolean equals (String name) {
		return this.name.toLowerCase() == name;
	}
	
	public String toString () {
		return name + " (" + tokenName + ")";
	}
	
//JAIL METHODS
	public boolean hasJailCard(){//checks if player has a get out of jail free card
		return jailCard;
	}
	
	public void giveJailCard(){//to be used with chance & community chest cards
		jailCard = true;
	}
	
	public void takeJailCard(){//use when player uses card whilst in jail
		jailCard = false;
	}
	
	public boolean inJail(){//check to see if the player is in jail
		return inJail;
	}
	
	public void goJail(){//the player is in jail
		inJail = true;
		position = JAIL;//moving the player to jail
	}
	
	public void escapeJail(){//the player is out of jail
		inJail = false;
	}
	
	public int getTime(){//checking how long a player has been in jail
		return timeInJail;
	}
	
	public void SpendTurnInJail(){//spending a turn in jail
		timeInJail++;
	}
}
