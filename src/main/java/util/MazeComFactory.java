package util;

import generated.*;

/**
 * Created by Martin Geßenich on 10.06.2017.
 * Factory that creates {@link MazeCom} objects from special combinations of data
 */
public final class MazeComFactory {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static MazeCom createMazeComMove(int playerID, MoveMessageType move) {
        MazeCom mc = objectFactory.createMazeCom();
        mc.setMcType(MazeComType.MOVE);
        mc.setId(playerID);

        mc.setMoveMessage(move);
        return mc;
    }

    public static MoveMessageType createMoveMessage(PositionType pinPosition, CardType shiftCard, PositionType shiftPosition){
        MoveMessageType move = objectFactory.createMoveMessageType();
        move.setNewPinPos(pinPosition);
        move.setShiftCard(shiftCard);
        move.setShiftPosition(shiftPosition);
        return move;
    }
}
