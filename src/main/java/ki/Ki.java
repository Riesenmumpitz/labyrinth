package ki;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    private static int directCounter = 0;
    private static int randomCounter = 0;

    public static MazeCom calculateTurn(MazeCom gameSituation) {

        MoveMessageType directMove = findDirectMove(gameSituation);
        if (directMove == null) {
            System.out.println("----- Random Move ");
            randomCounter++;
            System.out.println("randomCounter=" + randomCounter + " directCounter=" + directCounter);
            return generateRandomMove(gameSituation);
        } else {
            System.out.println("+++++ Direct Move ");
            directCounter++;
            System.out.println("randomCounter=" + randomCounter + " directCounter=" + directCounter);
            return MazeComFactory.createMazeComMove(gameSituation.getId(), directMove);
        }
    }

    private static MazeCom generateRandomMove(MazeCom gameSituation) {
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Card card = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardrotations = card.getPossibleRotations();
        int randomNum = ThreadLocalRandom.current().nextInt(0, cardrotations.size());
        Card shiftCard = cardrotations.get(randomNum);

//        Position playerPosition = getPlayerPosition(gameSituation, new Board(gameSituation.getAwaitMoveMessage().getBoard()));
        Position randomShiftPosition = getRandomShiftPosition(gameSituation);
        Position closestPlayerPosition = getClosestPlayerPosition(gameSituation, randomShiftPosition, shiftCard);
        MoveMessageType move = MazeComFactory.createMoveMessage(closestPlayerPosition, shiftCard, randomShiftPosition);

        // FIXME OLD VERSION
//        PositionType randomPlayerPosition = getRandomPlayerPosition(gameSituation, playerPosition);
//        MoveMessageType move = MazeComFactory.createMoveMessage(randomPlayerPosition, shiftCard, randomShiftPosition);


        return MazeComFactory.createMazeComMove(gameSituation.getId(), move);
    }

    private static Position getClosestPlayerPosition(MazeCom gameSituation, Position randomShiftPosition, Card currentCard) {
        Board board = new Board(gameSituation.getAwaitMoveMessage().getBoard());
        Position shiftedPlayerPosition = calculatePlayerPosition(randomShiftPosition, getPlayerPosition(gameSituation, board));
        Board fakeBoard = board.fakeShift(MazeComFactory.createMoveMessage(shiftedPlayerPosition, currentCard, randomShiftPosition));
        List<Position> listOfPositions = fakeBoard.getAllReachablePositions(shiftedPlayerPosition);
        Position treasurePosition = board.findTreasure(gameSituation.getAwaitMoveMessage().getTreasure());
        return findClosestPosition(listOfPositions, treasurePosition);
    }

    private static Position findClosestPosition(List<Position> listOfPositions, Position treasurePosition) {
        // Fall Karte schieben schmeißt Schatzkarte raus
        if (treasurePosition == null) {
            int random = ThreadLocalRandom.current().nextInt(0, listOfPositions.size());
            return listOfPositions.get(random);
        }

        // Normalfall
        int currentDistance = Integer.MAX_VALUE;
        List<Position> closestPositions = Lists.newArrayList();
        for (Position possiblePlayerPosition : listOfPositions) {
            int distance = calculateDistance(possiblePlayerPosition, treasurePosition);
            if (distance < currentDistance) {
                closestPositions.clear();
                closestPositions.add(possiblePlayerPosition);
                currentDistance = distance;
            } else if (distance == currentDistance) {
                closestPositions.add(possiblePlayerPosition);
            }
        }
//        System.out.println("Found closest space(s) " + closestPositions);
        int random = ThreadLocalRandom.current().nextInt(0, closestPositions.size());
        return closestPositions.get(random);
    }

    private static int calculateDistance(Position p, PositionType currentPlayerPosition) {
        int distance = Math.abs(currentPlayerPosition.getCol() - p.getCol()) + Math.abs(currentPlayerPosition.getRow() - p.getRow());
//        System.out.println("Distance between " + currentPlayerPosition + " and " + p + " is " + distance);
        return distance;
    }

    private static PositionType getRandomPlayerPosition(MazeCom gameSituation, PositionType playerPosition) {
        return playerPosition; // FIXME seems easier for me
        // FIXME dieser Code funktioniert nur ungenügend, da er nicht berücksichtigt, dass sich das board beim
        // FIXME verschieben ändert
//         AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
//         Board board = new Board(awaitMoveMessage.getBoard());
//         List<Position> allreachablePositions= board.getAllReachablePositions(playerPosition);
//         int random = ThreadLocalRandom.current().nextInt(0, allreachablePositions.size());
//         return allreachablePositions.get(random);
    }

    private static Position getRandomShiftPosition(MazeCom gameSituation) {
        // FIXME OLD
//        int zeile;
//        int spalte;
//        BoardType board = gameSituation.getAwaitMoveMessage().getBoard();
//        do {
//            zeile = ThreadLocalRandom.current().nextInt(0, 7);
//            spalte = ThreadLocalRandom.current().nextInt(0, 7);
//        } while (!loosePosition(zeile, spalte) && forbidden(createPositionType(zeile, spalte), board.getForbidden()));
//        return createPositionType(spalte, zeile);

        List<Position> possiblePositions = Position.getPossiblePositionsForShiftcard();
        int random;
        do {
            random = ThreadLocalRandom.current().nextInt(0, possiblePositions.size());
        } while (possiblePositions.get(random).equals(gameSituation.getAwaitMoveMessage().getBoard().getForbidden()));
        return possiblePositions.get(random);
    }

    private static boolean forbidden(PositionType positionType, PositionType forbidden) {
        if (forbidden == null) return false;
        if (positionType != null) {
            return positionType.equals(forbidden);
        }
        throw new NullPointerException();
    }

    private static MoveMessageType findDirectMove(MazeCom gameSituation) {
//        System.out.println("-----------------------------------------------------------------------");
//        System.out.println("-----------------------next calculation--------------------------------");
//        System.out.println("-----------------------------------------------------------------------");
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Board board = new Board(awaitMoveMessage.getBoard());
//        System.out.println(board.toString());
//        System.out.println("Player standing at " + getPlayerPosition(gameSituation, board));
//        System.out.println("Before card, player can move to :");
//        System.out.println(board.getAllReachablePositions(board.findPlayer(gameSituation.getId())));
//        System.out.println("Treasure at " + board.findTreasure(awaitMoveMessage.getTreasure()));
//        scanner.nextLine();
//        System.out.println("Current forbidden position = " + board.getForbidden());

//        System.out.println("Current shiftCard" + awaitMoveMessage.getBoard().getShiftCard());

        Card shiftCard = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardRotations = shiftCard.getPossibleRotations();
        List<Position> allowedPositionsList = Position.getPossiblePositionsForShiftcard().stream().filter(position -> !position.equals(board.getForbidden())).collect(Collectors.toList());
//        System.out.println("allowedPositionsList=" + allowedPositionsList);
//        System.out.println("Card rotations = " + cardRotations);

        for (Card currentRotatedCard : cardRotations) {
            for (Position currentCardPosition : allowedPositionsList) {
                PositionType shiftedPlayerposition = calculatePlayerPosition(currentCardPosition, getPlayerPosition(gameSituation, board));
                MoveMessageType fakeMove = MazeComFactory.createMoveMessage(shiftedPlayerposition, currentRotatedCard, currentCardPosition);
                Board fakeBoard = board.fakeShift(fakeMove);
                List<Position> reachablePositions = fakeBoard.getAllReachablePositions(shiftedPlayerposition);
                Position treasurePosition = fakeBoard.findTreasure(awaitMoveMessage.getTreasure());

                if (treasurePosition == null) continue;

                for (Position reachablePosition : reachablePositions) {
                    if (reachablePosition.equals(treasurePosition)) {
                        MoveMessageType directMove = MazeComFactory.createMoveMessage(reachablePosition, currentRotatedCard, currentCardPosition);
//                        System.out.println("Habe direkten move gefunden");
//                        System.out.println(directMove.toString());
//                        System.out.println(fakeBoard.toString());
//                        scanner.nextLine();

                        return directMove;
                    }
                }
            }
        }

//        for (Card c : cardRotations) {
//            for (int zeile = 0; zeile < 7; zeile++) {
//                if (zeile == 2 || zeile == 4) {
//                    System.out.println("zeile="+zeile+" ist verboten");
//                    continue;
//                }
//                for (int spalte = 0; spalte < 7; spalte++) {
//                    if (spalte == 2 || spalte == 4) {
//                        System.out.println("spalte="+spalte+" ist verboten");
//                        continue;
//                    }
//                    if (!loosePosition(zeile, spalte)) {
//                        System.out.println("zeile="+zeile+" spalte="+spalte+" ist keine lose Position");
//                        continue;
//                    }
//
//                    PositionType fakeShiftCardPosition = createPositionType(zeile, spalte);
//                    if (forbidden(createPositionType(zeile,spalte),board.getForbidden())){
//                        System.out.println("zeile="+zeile+" spalte="+spalte+" ist verboten");
//                        continue;
//                    }
//
//                    Position playerposition = getPlayerPosition(gameSituation, board);
//
//                    if (playerposition.getRow() == zeile){
//                        if (spalte == 0) playerposition.setCol((playerposition.getCol()+1)%6);
//                        else playerposition.setCol((playerposition.getCol()-1+6)%6);
//                    }
//
////                    System.out.println("Current orientation " + c);
//
//                    MoveMessageType fakeMove = new MoveMessageType();
//                    fakeMove.setShiftPosition(fakeShiftCardPosition);
//                    fakeMove.setShiftCard(c);
//                    fakeMove.setNewPinPos(playerposition);
//                    Board fakeBoard = board.fakeShift(fakeMove);
//
//
//                    List<Position> reachablePositions = fakeBoard.getAllReachablePositions(playerposition);
//                    Position treasurePosition = new Position(fakeBoard.findTreasure(awaitMoveMessage.getTreasure()));
//                    for (Position p : reachablePositions) {
//                        if (p.equals(treasurePosition)) {
//                            // TODO REMOVE >>>>>>>>>
////                    System.out.println("zeile = " + zeile + " spalte = " + spalte );
//                            System.out.println("!!!!!!!!!!!!!!!!!!!! Found direct move!!!");
//                            System.out.println("After entering card at "+ fakeShiftCardPosition + ", player can move to :");
//                            System.out.println(reachablePositions);
//                    System.out.println(fakeBoard);
////                    scanner.nextLine();
//                            // TODO REMOVE <<<<<<<<<<
//                            ret.setNewPinPos(p);
//                            ret.setShiftCard(c);
//                            ret.setShiftPosition(fakeShiftCardPosition);
////                            System.out.println("Habe direkten move gefunden");
////                            System.out.println(ret.toString());
////                            scanner.nextLine();
//                            return ret;
//                        }
//                    }
//                }
//            }
//        }
        return null;
    }

    public static Position calculatePlayerPosition(Position cardPosition, Position playerPosition) {
//        System.out.println("calculatePlayerPosition mit " + "cardPosition=" + cardPosition + " and playerPosition=" + playerPosition);
        if (cardPosition.isOppositePosition(playerPosition)) {
            // Karte schiebt eigenen Spieler vom Feld und wird gegenüber wieder aufgestellt
//            System.out.println("Karte wird gegenüber von Spieler eingeschoben");
            return cardPosition;
        }
//        else if (cardPosition.equals(playerPosition)) {
//            if (cardPosition.getCol() == 0)
//                return new Position()
//        }
        else if (cardPosition.getCol() == playerPosition.getCol()) {
//            System.out.println("Karte wird in gleiche Spalte wie Spieler eingeschoben");
            if (cardPosition.getRow() == 0)
                return new Position(playerPosition.getRow() + 1, playerPosition.getCol());
            else if (cardPosition.getRow() == 6)
                return new Position(playerPosition.getRow() - 1, playerPosition.getCol());
        } else if (cardPosition.getRow() == playerPosition.getRow()) {
//            System.out.println("Karte wird in gleiche Zeile wie Spieler eingeschoben");
            if (cardPosition.getCol() == 0)
                return new Position(playerPosition.getRow(), playerPosition.getCol() + 1);
            else if (cardPosition.getCol() == 6)
                return new Position(playerPosition.getRow(), playerPosition.getCol() - 1);
        }
        return playerPosition;
    }

    private static int plusOne(int number) {
        return (number + 1) % 7;
    }

//    /**
//     * Nur temporär, weil ich dachte, die Werte zu vertauschen hilft, es werden aber alle vertauscht zurückgegeben,
//     * daher sind es zu viele um alles mitzuhalten...
//     */
//    @Deprecated
//    private static List<Position> flip(List<Position> positions) {
//        List<Position> result = Lists.newArrayList();
//        for (int i = 0; i < positions.size(); i++) {
//            result.add(positions.get(i).flip());
//        }
//        return result;
//    }

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
