/*
 * E�mear O'Shea Breen - 15487912
 * Siobh�n O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
public class Station extends Property {

	private int[] rentTable;
	
	Station (String name, String shortName, int price, int mortgageValue, int[] rentTable) {
		super(name, price, shortName,mortgageValue);
		this.rentTable = rentTable;
		return;
	}
	
	public int getRent () {
		return rentTable[super.getOwner().getNumStationsOwned()-1];
	}
	
}
