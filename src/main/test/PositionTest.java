import generated.PositionType;
import helpers.Position;
import javafx.geometry.Pos;
import ki.Ki;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martin Geßenich on 29.06.2017.
 */
public class PositionTest {

    @Test
    @Ignore
    public void testThatPosittionListIsCorrect() {
        List<Position> list = Position.getPossiblePositionsForShiftcard();
        System.out.println(list);
        PositionType forbidden = new PositionType();
        forbidden.setCol(0);
        forbidden.setRow(3);
        List<Position> newlist = list.stream().filter(position -> !position.equals(forbidden)).collect(Collectors.toList());
        System.out.println(newlist);
        Assert.assertFalse(list.size() == newlist.size());
    }

    ///////////////////////////////////////////////////////
    // NORMAL
    ///////////////////////////////////////////////////////

    @Test
    public void calculatePlayerPositionTestLeft() {
        Position cardPosition = new Position(3, 0);
        Position playerPosition = new Position(3, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(3, 4);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestRight() {
        Position cardPosition = new Position(3, 6);
        Position playerPosition = new Position(3, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(3, 2);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestTop() {
        Position cardPosition = new Position(0, 3);
        Position playerPosition = new Position(3, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(4, 3);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestBottom() {
        Position cardPosition = new Position(6, 3);
        Position playerPosition = new Position(3, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(2, 3);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    ///////////////////////////////////////////////////////
    // OPPOSITE
    ///////////////////////////////////////////////////////

    @Test
    public void calculatePlayerPositionTestLeftOpposite() {
        Position cardPosition = new Position(0, 1);
        Position playerPosition = new Position(6, 1);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(0, 1);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestRightOpposite() {
        Position cardPosition = new Position(6, 1);
        Position playerPosition = new Position(0, 1);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(6, 1);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestTopOpposite() {
        Position cardPosition = new Position(0, 3);
        Position playerPosition = new Position(6, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(0, 3);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestBottomOpposite() {
        Position cardPosition = new Position(6, 3);
        Position playerPosition = new Position(0, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(6, 3);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    ///////////////////////////////////////////////////////
    // ADJACENT BUT EDGE
    ///////////////////////////////////////////////////////

    @Test
    public void calculatePlayerPositionTestLeftEdge() {
        Position cardPosition = new Position(5, 0);
        Position playerPosition = new Position(6, 0);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(6, 0);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestRightEdge() {
        Position cardPosition = new Position(3, 6);
        Position playerPosition = new Position(6, 6);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(6,6);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestTopEdge() {
        Position cardPosition = new Position(0, 5);
        Position playerPosition = new Position(0, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(0, 3);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }

    @Test
    public void calculatePlayerPositionTestBottomEdge() {
        Position cardPosition = new Position(6, 1);
        Position playerPosition = new Position(6, 3);
        Position newPlayerPosition = Ki.calculatePlayerPosition(cardPosition, playerPosition);
//        System.out.println("newPlayerPosition=" + newPlayerPosition);
        Position expectedPlayerPosition = new Position(6, 3);
        Assert.assertTrue(expectedPlayerPosition.equals(newPlayerPosition));
    }
}
