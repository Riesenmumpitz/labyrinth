package ki;

import com.google.common.collect.Lists;
import generated.AwaitMoveMessageType;
import generated.MazeCom;
import generated.MoveMessageType;
import generated.PositionType;
import helpers.Board;
import helpers.Card;
import helpers.Position;
import util.MazeComFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Martin on 10.06.2017.
 * reworked by Dana, Marie, Martin on 21.06.2017
 */
public final class Ki {

    public static MazeCom calculateTurn(MazeCom gameSituation) {

        MoveMessageType directMove = findDirectMove(gameSituation);
        if (directMove == null) {
            return generateRandomMove(gameSituation);
        } else {
            return MazeComFactory.createMazeComMove(gameSituation.getId(), directMove);
        }
    }

    private static MazeCom generateRandomMove(MazeCom gameSituation) {
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Card card = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardrotations = card.getPossibleRotations();
        int randomNum = ThreadLocalRandom.current().nextInt(0, cardrotations.size());
        Card shiftCard = cardrotations.get(randomNum);

        Position randomShiftPosition = getRandomShiftPosition(gameSituation);
        Position closestPlayerPosition = getClosestPlayerPosition(gameSituation, randomShiftPosition, shiftCard);
        MoveMessageType move = MazeComFactory.createMoveMessage(closestPlayerPosition, shiftCard, randomShiftPosition);

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
        int random = ThreadLocalRandom.current().nextInt(0, closestPositions.size());
        return closestPositions.get(random);
    }

    private static int calculateDistance(Position p, PositionType currentPlayerPosition) {
        return Math.abs(currentPlayerPosition.getCol() - p.getCol()) + Math.abs(currentPlayerPosition.getRow() - p.getRow());
    }

    private static Position getRandomShiftPosition(MazeCom gameSituation) {
        List<Position> possiblePositions = Position.getPossiblePositionsForShiftcard();
        int random;
        do {
            random = ThreadLocalRandom.current().nextInt(0, possiblePositions.size());
        } while (possiblePositions.get(random).equals(gameSituation.getAwaitMoveMessage().getBoard().getForbidden()));
        return possiblePositions.get(random);
    }

    private static MoveMessageType findDirectMove(MazeCom gameSituation) {
        AwaitMoveMessageType awaitMoveMessage = gameSituation.getAwaitMoveMessage();
        Board board = new Board(awaitMoveMessage.getBoard());

        Card shiftCard = new Card(awaitMoveMessage.getBoard().getShiftCard());
        List<Card> cardRotations = shiftCard.getPossibleRotations();
        List<Position> allowedPositionsList = Position.getPossiblePositionsForShiftcard().stream().filter(position -> !position.equals(board.getForbidden())).collect(Collectors.toList());

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
                        return MazeComFactory.createMoveMessage(reachablePosition, currentRotatedCard, currentCardPosition);
                    }
                }
            }
        }
        return null;
    }

    public static Position calculatePlayerPosition(Position cardPosition, Position playerPosition) {
        if (cardPosition.isOppositePosition(playerPosition)) {
            // Karte schiebt eigenen Spieler vom Feld und wird gegenüber wieder aufgestellt
            return cardPosition;
        }
        else if (cardPosition.getCol() == playerPosition.getCol()) {
            if (cardPosition.getRow() == 0)
                return new Position(playerPosition.getRow() + 1, playerPosition.getCol());
            else if (cardPosition.getRow() == 6)
                return new Position(playerPosition.getRow() - 1, playerPosition.getCol());
        } else if (cardPosition.getRow() == playerPosition.getRow()) {
            if (cardPosition.getCol() == 0)
                return new Position(playerPosition.getRow(), playerPosition.getCol() + 1);
            else if (cardPosition.getCol() == 6)
                return new Position(playerPosition.getRow(), playerPosition.getCol() - 1);
        }
        return playerPosition;
    }

    private static Position getPlayerPosition(MazeCom gameSituation, Board board) {
        return board.findPlayer(gameSituation.getId());
    }
}
