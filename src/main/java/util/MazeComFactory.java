package util;

import generated.*;

/**
 * Created by Martin on 10.06.2017.
 */
public class MazeComFactory {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static MazeCom createMoveMessage(int playerID, MoveMessageType move) {
        MazeCom mc = objectFactory.createMazeCom();
        mc.setMcType(MazeComType.MOVE);
        mc.setId(playerID);

        mc.setMoveMessage(move);
        return mc;
    }
}
