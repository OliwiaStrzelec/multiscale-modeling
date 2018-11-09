package oliwiastrzelec.multiscalemodeling.model;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import java.util.*;

public class MultiscaleModelHelper {
    public static String getDataFile(Cell[][] array, int sizeX, int sizeY) {
        StringBuilder sb = new StringBuilder();
        sb.append("x\ty\tid\tr\tg\tb\tstate\n");
        Cell cell;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                cell = array[i][j];
                sb.append(i + "\t" + j + "\t" + cell.getId() + "\t" + cell.getRgb()[0] + "\t" + cell.getRgb()[1] + "\t" + cell.getRgb()[2] + "\t" + cell.getState() + "\n");
            }
        }
        return sb.toString();
    }

    public static void printArray(Cell[][] array) {
        List<Cell[]> list = new ArrayList(Arrays.asList(array));
        list.forEach(cells -> System.out.println(Arrays.asList(cells)));
    }

    public static int[] generateRandomColor() {
        int[] color = new int[3];
        color[0] = (int) (Math.floor(Math.random() * 220) + 36);
        color[1] = 0;//(int) (Math.floor(Math.random() * 255) + 1);
        color[2] = (int) (Math.floor(Math.random() * 220) + 36);
        return color;
    }

    private static Cell generateRandomCell(int x, int y) {
        Cell cell = new Cell(Integer.valueOf(String.valueOf(x) + String.valueOf(y)));
        cell.setRgb(generateRandomColor());
        return cell;
    }

    public static int countEmptyCells(Cell[][] array) {
        int count = 0;
        for (Cell[] cells : array) {
            for (Cell cell : cells) {
                if (cell.getId() == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    static List<Cell> generateRandomCells(int numberOfNucleons) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 1; i <= numberOfNucleons; i++) {
            Cell c = new Cell(i);
            c.setRgb(generateRandomColor());
            cells.add(c);
        }
        return cells;
    }

    static Cell[][] generateEmptyArray(int sizeX, int sizeY) {
        Cell[][] cells = new Cell[sizeX][sizeY];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeY; j++) {
                Cell c = new Cell(0);
                c.setRgb(new int[]{255, 255, 255});
                cells[i][j] = c;
            }
        }
        return cells;
    }
}
