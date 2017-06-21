package ki;

import generated.*;
import helpers.Card;
import util.MazeComFactory;

import java.util.Scanner;

/**
 * Created by Martin Geßenich on 11.06.2017.
 * Replacement for KI player as long as no KI is programmed
 */
@SuppressWarnings("SpellCheckingInspection")
public class Human {

    private static ObjectFactory objectFactory = new ObjectFactory();
    private static Scanner scanner = new Scanner(System.in);


    static MazeCom move(MazeCom gameSituation) {


        MoveMessageType move = boardTest(gameSituation);


        return MazeComFactory.createMazeComMove(gameSituation.getId(), move);
    }

    private static MoveMessageType boardTest(MazeCom gameSituation) {
//        Board board = new Board(gameSituation.getAwaitMoveMessage().getBoard());

        MoveMessageType move = null;
        Card shiftCard = new Card(gameSituation.getAwaitMoveMessage().getBoard().getShiftCard());
        System.out.println(shiftCard.toString());
        System.out.println(shiftCard.getPossibleRotations());
//        System.out.println(board.toString());

//        while (true) {

        move = objectFactory.createMoveMessageType();

        PositionType shiftposition = objectFactory.createPositionType();
        shiftposition.setRow(getInt("Row for ShiftPosition"));
        shiftposition.setCol(getInt("Column for ShiftPosition"));
        move.setShiftPosition(shiftposition);

        int rotated = getInt("Wie viel Grad drehen");
        CardType shiftcard = new Card(shiftCard.getShape(), Card.Orientation.fromValue((rotated+180)% 360), shiftCard.getTreasure());


        move.setShiftCard(shiftcard);

        PositionType pinPosition = objectFactory.createPositionType();
        pinPosition.setRow(getInt("Row for pin position"));
        pinPosition.setCol(getInt("Column for Pin position"));
        move.setNewPinPos(pinPosition);

//            board.proceedShift(move);

//            System.out.println(board.toString());
//            if (scanner.nextLine() == "")
//                System.exit(0);
//        }

        return move;
    }

    private static String getString(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private static int getInt(String message) {
        System.out.println(message);
        return scanner.nextInt();
    }
}
