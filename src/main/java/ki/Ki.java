package ki;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;
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
    private static Scanner scanner = new Scanner(System.in);

    public static MazeCom calculateTurn(MazeCom gameSituation) {

        MoveMessageType directMove = findDirectMove(gameSituation);
        if (directMove == null) {
            return generateRandomMove(gameSituation);
        } else {
            return MazeComFactory.createMazeComMove(gameSituation.getId(),directMove);
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
        if (forbidden == null) return false;
        if (positionType != null) {
            return positionType.equals(forbidden);
        }
        throw new NullPointerException();
    }

    private static MoveMessageType findDirectMove(MazeCom gameSituation) {
        MoveMessageType ret = new MoveMessageType();
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Board board = new Board(awaitMoveMessage.getBoard());
//        System.out.println(board.toString());
        System.out.println("Before card, player can move to :");
        System.out.println(board.getAllReachablePositions(board.findPlayer(gameSituation.getId())));
        System.out.println("Treasure at " + new Position(board.findTreasure(awaitMoveMessage.getTreasure())));
        scanner.nextLine();

        System.out.println("Current shiftCard" + awaitMoveMessage.getBoard().getShiftCard());

        Card card = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardRotations = card.getPossibleRotations();

        for (Card c : cardRotations) {
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

                    PositionType fakeShiftCardPosition = createPositionType(zeile, spalte);
                    if (forbidden(createPositionType(zeile,spalte),board.getForbidden())){
                        continue;
                    }

                    System.out.println("Current orientation " + c);

                    MoveMessageType fakeMove = new MoveMessageType();
                    fakeMove.setShiftPosition(fakeShiftCardPosition);
                    fakeMove.setShiftCard(c);
                    Position playerposition = getPlayerPosition(gameSituation, board);
                    fakeMove.setNewPinPos(playerposition);
                    Board fakeBoard = board.fakeShift(fakeMove);


                    List<Position> reachablePositions = fakeBoard.getAllReachablePositions(playerposition);
                    // TODO REMOVE >>>>>>>>>
                    System.out.println("zeile = " + zeile + " spalte = " + spalte );
                    System.out.println("After entering card at "+ fakeShiftCardPosition + ", player can move to :");
                    System.out.println(reachablePositions);
                    System.out.println(fakeBoard);
                    scanner.nextLine();
                    // TODO REMOVE <<<<<<<<<<
                    Position treasurePosition = new Position(fakeBoard.findTreasure(awaitMoveMessage.getTreasure()));
                    for (Position p : reachablePositions) {
                        if (p.equals(treasurePosition)) {
                            ret.setNewPinPos(p);
                            ret.setShiftCard(c);
                            ret.setShiftPosition(fakeShiftCardPosition);
//                            System.out.println("Habe direkten move gefunden");
//                            System.out.println(ret.toString());
//                            scanner.nextLine();
                            return ret;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Nur temporär, weil ich dachte, die Werte zu vertauschen hilft, es werden aber alle vertauscht zurückgegeben,
     * daher sind es zu viele um alles mitzuhalten...
     */
    @Deprecated
    private static List<Position> flip(List<Position> positions) {
        List<Position> result = Lists.newArrayList();
        for (int i = 0; i < positions.size(); i++) {
            result.add(positions.get(i).flip());
        }
        return result;
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
