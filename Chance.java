/*
 * Eímear O'Shea Breen - 15487912
 * Siobhán O'Sullivan - 15519453
 * Eoghan McDermott - 15345451
 */
public class Chance {
	
	private final static int GO = 0;
	private final static int PALL_MALL = 11;
	private final static int MARYLEBONE_STATION = 15;
	private final static int TRAFALGAR_SQ = 24;
	private final static int MAYFAIR = 39;
	
	int i=0;//will use this to count through the cards
	
	private String ChanceMessages[] = {
			"Advance to Go.",
			"Go to jail. Move directly to jail. Do not pass Go. Do not collect £200.",
			"Advance to Pall Mall. If you pass Go collect £200.",
			"Take a trip to Marylebone Station and if you pass Go collect £200.",
			"Advance to Trafalgar Square. If you pass Go collect £200.",
			"Advance to Mayfair.",
			"Go back three spaces.",
			"Make general repairs on all of your houses. For each house pay £25. For each hotel pay £100.",
			"You are assessed for street repairs: £40 per house, £115 per hotel.",
			"Pay school fees of £150.",
			"Drunk in charge fine £20.",
			"Speeding fine £15.",
			"Your building loan matures. Receive £150.",
			"You have won a crossword competition. Collect £100.",
			"Bank pays you dividend of £50.",
			"Get out of jail free. This card may be kept until needed or sold."
	};

	public String getMessage()
	{
		return ChanceMessages[i++];//increment after to move to the next card
	}
	
	public void drawChance(Player player)//maybe make it return a string - the info one - solves ui problem
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
				player.goJail();//player is now in jail
				break;
				
			case 2:
				while(!(player.getPosition() == PALL_MALL))
					player.move(+1);//move forward until player reaches pall mall
				if(player.passedGo())
					player.doTransaction(+200);
				break;
				
			case 3:
				while(!(player.getPosition() == MARYLEBONE_STATION))
					player.move(+1);//move forward until player reaches marylebone station
				if(player.passedGo())
					player.doTransaction(+200);
				break;
					
			case 4:
				while(!(player.getPosition() == TRAFALGAR_SQ))
					player.move(+1);//move forward until player reaches trafalgar sq				
				if(player.passedGo())
					player.doTransaction(+200);
				break;
				
			case 5:
				while(!(player.getPosition() == MAYFAIR))
					player.move(+1);//move forward until player reaches mayfair				
				if(player.passedGo())
					player.doTransaction(+200);
				break;
			
			case 6:
				player.move(-3);//move the player back 3 spaces
				break;
			
			case 7:
				for(int j=0;j<player.getProperties().size();j++)
				{
					Site site = (Site) player.getProperties().get(j);//casting is definitely a bad idea here but sure look
					if(site.getNumBuildings() > 0)//if there are buildings on the site
						if(site.getNumBuildings() == 5)//if there is a hotel built
							player.doTransaction(-100);//subtract 100 for the hotel
						else//no hotels just houses
							player.doTransaction(-25*site.getNumBuildings());//subtract 25 for each house
				}	
				break;
				
			case 8:
				for(int j=0;j<player.getProperties().size();j++)
				{
					Site site = (Site) player.getProperties().get(j);//casting is definitely a bad idea here but sure look
					if(site.getNumBuildings() > 0)//if there are buildings on the site
						if(site.getNumBuildings() == 5)//if there is a hotel built
							player.doTransaction(-115);//subtract 115 for the hotel
						else//no hotels just houses
							player.doTransaction(-45*site.getNumBuildings());//subtract 45 for each house
				}
				break;
				
			case 9:
				player.doTransaction(-150);
				break;
			
			case 10:
				player.doTransaction(-20);				
				break;
		
			case 11:
				player.doTransaction(-15);				
				break;
				
			case 12:
				player.doTransaction(150);
				break;
			
			case 13:
				player.doTransaction(100);
				break;
				
			case 14:
				player.doTransaction(50);
				break;
			
			case 15:
				player.giveJailCard();//giving the player a get out of jail free card
				i=0;//resetting the deck
				break;
		}
	}
}
