package ki;

import java.util.List;

import generated.AwaitMoveMessageType;
import generated.CardType;
import generated.MazeCom;
import generated.MoveMessageType;
import generated.ObjectFactory;
import generated.PositionType;
import helpers.Board;
import helpers.Card;
import helpers.Position;
import util.MazeComFactory;

/**
 * Created by Martin on 10.06.2017.
 */
public final class Ki {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static MazeCom calculateTurn(MazeCom gameSituation) {

    	MoveMessageType directmove = findDirectMove(gameSituation);
    	if (directmove == null){
    		//TODO Fall: alternativer move
    	} else {
    		MazeCom ret = new MazeCom();
    		ret.setMoveMessage(directmove);
    		return ret;
    		
    	}
        return new MazeCom();
    }

	private static MoveMessageType findDirectMove(MazeCom gameSituation) {
		MoveMessageType ret = new MoveMessageType();
		AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
		Board board = new Board(awaitMoveMessage.getBoard());
		Card card = new Card(awaitMoveMessage.getBoard().getShiftCard());
		List<Card> cardrotations = card.getPossibleRotations();
		for (Card c : cardrotations){
			for (int zeile = 0; zeile<7; zeile++){
				if (zeile== 2|| zeile==4){
					continue;
				}
				for (int spalte = 0; spalte<7; spalte++){
					if (spalte== 2|| spalte ==4){
						continue;
					}
					PositionType position = new PositionType();
					position.setRow(zeile);
					position.setCol(spalte);
					if (position.equals(board.getForbidden())){
						continue;
					}
					
					MoveMessageType move = new MoveMessageType();
					move.setShiftPosition(position);
					move.setShiftCard(c);
					Position playerposition = board.findPlayer(gameSituation.getId());
					move.setNewPinPos(playerposition);
					board.fakeShift(move);
					
					List<Position> reachablepositions = board.getAllReachablePositions(playerposition);
					Position treasureposition = new Position(board.findTreasure(awaitMoveMessage.getTreasure()));
					for (Position p : reachablepositions){
						if (p.equals(treasureposition)){
							ret.setNewPinPos(p);
							ret.setShiftCard(c);
							ret.setShiftPosition(position);
							return ret;
						}
					}
				}
			}
		}
		return null;
	}
    
   
}
