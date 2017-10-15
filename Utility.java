/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
public class Utility extends Property {
	
	int[] rentTable;
	Dice dice;
	
	Utility (String name, String shortName, int price, int mortgageValue, int[] rentTable, Dice dice) {
		super(name, price, shortName, mortgageValue);
		this.rentTable = rentTable;
		this.dice = dice;
		return;
	}
	
	public int getRentMultiplier () {
		return rentTable[super.getOwner().getNumUtilitiesOwned()-1];
	}

	public int getRent () {
		return dice.getTotal() * getRentMultiplier();
	}
	
}
