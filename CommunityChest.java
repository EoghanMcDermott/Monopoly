/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
import javax.swing.JOptionPane;


public class CommunityChest {
	
	private static final int GO = 0;
	private static final int OLD_KENT_RD = 1;
	
	int i =0;//will use this to count through the cards
	
	private String CommunityMessages[] = {
			"Advance to Go.",
			"Go back to Old Kent Road.",
			"Go to jail. Move directly to jail. Do not pass Go. Do not collect £200.",
			"Pay hospital £100.",
			"Doctor's fee. Pay £50.",
			"Pay your insurance premium £50.",
			"Bank error in your favour. Collect £200.",
			"Annuity matures. Collect £100.",
			"You inherit £100.",
			"From sale of stock you get £50.",
			"Receive interest on 7% preference shares: £25.",
			"Income tax refund. Collect £20.",
			"You have won second prize in a beauty contest. Collect £10.",
			"It is your birthday. Collect £10 from each player.",
			"Get out of jail free. This card may be kept until needed or sold.",
			"Pay a £10 fine or take a Chance."
	};
	
	public String getMessage(){
		return CommunityMessages[i++];//increment after to move to the next card
		
	}
	
	public void drawCommunity(Player player, Players players)
	{
		switch(i)
		{
			case 0:
				while(!(player.getPosition()== GO))
					player.move(+1);//moving forward 1 square until player reaches go
				if(player.passedGo())
					player.doTransaction(+200);
				break;
				
			case 1:
				while(!(player.getPosition()== OLD_KENT_RD))
					player.move(-1);//moving back 1 square until player reaches old kent rd				
				break;
				
			case 2:
				player.goJail();
				break;
		
			case 3:
				player.doTransaction(-100);		
				break;
				
			case 4:
				player.doTransaction(-50);
				break;
				
			case 5:
				player.doTransaction(-50);
				break;
				
			case 6:
				player.doTransaction(+200);
				break;
				
			case 7:
				player.doTransaction(+100);
				break;
				
			case 8:
				player.doTransaction(+100);				
				break;
				
			case 9:
				player.doTransaction(+50);				
				break;
				
			case 10:
				player.doTransaction(+25);				
				break;
			
			case 11:
				player.doTransaction(+20);
				break;
			
			case 12:
				player.doTransaction(10);				
				break;
			
			case 13:
				for(int j=0; j<players.numPlayers();j++)
				{
					players.get().get(j).doTransaction(-10);//taking 10 from each player
					player.doTransaction(+10);//giving 10 to current player
				}
				break;
				
			case 14:
				player.giveJailCard();//giving the player a get out of jail free card
				break;
				
			case 15://have to use a joptionpane to get user input for the pay fine - no ui interaction here unfortunately
				String input = JOptionPane.showInputDialog("Please enter \"pay fine\" to pay £10 to pay or anything else to draw another card");
				if(input.equalsIgnoreCase("pay fine"))
					player.doTransaction(-10);
				else
				{
					i=0;
					drawCommunity(player, players);//back to the start of the deck
				}
		}
	}
}
