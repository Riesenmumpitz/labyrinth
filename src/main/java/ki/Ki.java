package ki;

import generated.MazeCom;
import generated.ObjectFactory;
import util.MazeComFactory;

/**
 * Created by Martin on 10.06.2017.
 */
public final class Ki {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static MazeCom calculateTurn(MazeCom gameSituation) {

        return Human.move(gameSituation);
    }
}
