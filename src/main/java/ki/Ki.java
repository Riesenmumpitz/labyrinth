package ki;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import generated.*;
import helpers.Board;
import helpers.Card;
import helpers.Position;
import util.MazeComFactory;

/**
 * Created by Martin on 10.06.2017.
 * reworked by Dana, Marie, Martin on 21.06.2017
 */
// branch 'master' of https://github.com/Riesenmumpitz/labyrinth.git
public final class Ki {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static MazeCom calculateTurn(MazeCom gameSituation) {

        MoveMessageType directmove = findDirectMove(gameSituation);
        if (directmove == null) {
            return generateRandomMove(gameSituation);
        } else {
            MazeCom ret = new MazeCom();
            ret.setMoveMessage(directmove);
            return ret;
        }
    }

    private static MazeCom generateRandomMove(MazeCom gameSituation) {
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Card card = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardrotations = card.getPossibleRotations();
        int randomNum = ThreadLocalRandom.current().nextInt(0, cardrotations.size());

        CardType shiftCard = cardrotations.get(randomNum);
        PositionType playerPosition = getPlayerPosition(gameSituation, new Board(gameSituation.getAwaitMoveMessage().getBoard()));
        PositionType randomPlayerPosition = getRandomPlayerPosition(gameSituation, playerPosition);
        PositionType randomShiftPosition = getRandomShiftPosition(gameSituation);

        MoveMessageType move = MazeComFactory.createMoveMessage(randomPlayerPosition, shiftCard, randomShiftPosition);

        return MazeComFactory.createMazeComMove(gameSituation.getId(),move);
    }
    
    private static PositionType getRandomPlayerPosition(MazeCom gameSituation, PositionType playerPosition){
    	 //MoveMessageType ret = new MoveMessageType();
         AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
         Board board = new Board(awaitMoveMessage.getBoard());
         List<Position> allreachablePositions= board.getAllReachablePositions(playerPosition);
         int random = ThreadLocalRandom.current().nextInt(0, allreachablePositions.size());
         return allreachablePositions.get(random);
         
    	
    }

    private static PositionType getRandomShiftPosition(MazeCom gameSituation) {
        int zeile;
        int spalte;
        BoardType board = gameSituation.getAwaitMoveMessage().getBoard();
        do {
            zeile = ThreadLocalRandom.current().nextInt(0, 7);
            spalte = ThreadLocalRandom.current().nextInt(0, 7);
        } while (!loosePosition(zeile, spalte) && !forbidden(createPositionType(zeile, spalte), board.getForbidden()));
        return createPositionType(zeile, spalte);
    }

    private static boolean forbidden(PositionType positionType, PositionType forbidden) {
        if (positionType != null) {
            return positionType.equals(forbidden);
        }
        throw new NullPointerException();
    }

    private static MoveMessageType findDirectMove(MazeCom gameSituation) {
        MoveMessageType ret = new MoveMessageType();
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Board board = new Board(awaitMoveMessage.getBoard());

        Card card = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardrotations = card.getPossibleRotations();

        for (Card c : cardrotations) {
            for (int zeile = 0; zeile < 7; zeile++) {
                if (zeile == 2 || zeile == 4) {
                    continue;
                }
                for (int spalte = 0; spalte < 7; spalte++) {
                    if (spalte == 2 || spalte == 4) {
                        continue;
                    }
                    if (!loosePosition(zeile, spalte)) {
                        continue;
                    }

                    PositionType position = createPositionType(zeile, spalte);
                    if (position.equals(board.getForbidden())) {
                        continue;
                    }

                    MoveMessageType move = new MoveMessageType();
                    move.setShiftPosition(position);
                    move.setShiftCard(c);
                    Position playerposition = getPlayerPosition(gameSituation, board);
                    move.setNewPinPos(playerposition);
                    board.fakeShift(move);

                    List<Position> reachablepositions = board.getAllReachablePositions(playerposition);
                    Position treasureposition = new Position(board.findTreasure(awaitMoveMessage.getTreasure()));
                    for (Position p : reachablepositions) {
                        if (p.equals(treasureposition)) {
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

    private static PositionType createPositionType(int zeile, int spalte) {
        PositionType position = new PositionType();
        position.setRow(zeile);
        position.setCol(spalte);
        return position;
    }

    private static Position getPlayerPosition(MazeCom gameSituation, Board board) {
        return board.findPlayer(gameSituation.getId());
    }

    private static boolean loosePosition(int zeile, int spalte) {
        return (zeile >= 0 && zeile <= 6 && zeile % 6 == 0 && spalte >= 0 && spalte <= 6 && spalte % 2 != 0) ||
                (spalte >= 0 && spalte <= 6 && spalte % 6 == 0 && zeile >= 0 && zeile <= 6 && zeile % 2 != 0);
    }


}
