package oliwiastrzelec.multiscalemodeling.model;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import java.util.*;

public class MooreNeighbourhood {
    private static void mooreNeighbourhood(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        Cell c = previousArray[x][y];
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((i < 0 || i > previousArray.length) || (j < 0 || j > previousArray[0].length)) {
                    continue;
                }
                if (previousArray[i][j].getId() == 0) {
//                    fillMooreNeighbour(i, j, previousArray, array);
                    currentArray[i][j].setId(c.getId());
                    currentArray[i][j].setRgb(c.getRgb());
                } else {
                    currentArray[i][j].setId(previousArray[i][j].getId());
                    currentArray[i][j].setRgb(previousArray[i][j].getRgb());
                }
            }
        }
    }

    static void fillMooreNeighbour(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        List<Cell> grainsCount = new ArrayList<>();
        Cell c;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                addGrainToList(previousArray, grainsCount, i, j);
            }
        }
        if (grainsCount.isEmpty()) {
            return;
        }

        c = getMostCommonCell(grainsCount);
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    static void addGrainToList(Cell[][] previousArray, List<Cell> grainsCount, int i, int j) {
        Cell c;
        if (i < 0 || i >= previousArray.length || j < 0 || j >= previousArray[0].length) {
            return;
        }
        if ((c = previousArray[i][j]).getId() != 0 && c.getId() != -1 && c.getId() != -2 && c.getId() != -3 && c.getId() != -4) {
            grainsCount.add(c);
        }
    }

    static void fillMooreNeighbourFourthRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray, int probability) {
        int r = new Random().nextInt(100);
        if (r < probability) {
            fillMooreNeighbour(x, y, previousArray, currentArray);
        }
    }

    static void fillMooreNeighbourThirdRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray, int probability) {
        List<Cell> grainsCount = new ArrayList<>();
        addGrainToList(previousArray, grainsCount, x - 1, y - 1);
        addGrainToList(previousArray, grainsCount, x + 1, y + 1);
        addGrainToList(previousArray, grainsCount, x - 1, y + 1);
        addGrainToList(previousArray, grainsCount, x + 1, y - 1);
        if (grainsCount.isEmpty()) {
            fillMooreNeighbourFourthRule(x, y, previousArray, currentArray, probability);
            return;
        }
        Cell c = threeOrMoreOccurrences(grainsCount);
        if (c == null) {
            fillMooreNeighbourFourthRule(x, y, previousArray, currentArray, probability);
            return;
        }
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    static void fillMooreNeighbourSecondRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray, int probability) {
        List<Cell> grainsCount = new ArrayList<>();
        addGrainToList(previousArray, grainsCount, x - 1, y);
        addGrainToList(previousArray, grainsCount, x + 1, y);
        addGrainToList(previousArray, grainsCount, x, y - 1);
        addGrainToList(previousArray, grainsCount, x, y + 1);
        if (grainsCount.isEmpty()) {
            fillMooreNeighbourThirdRule(x, y, previousArray, currentArray, probability);
            return;
        }
        Cell c = threeOrMoreOccurrences(grainsCount);
        if (c == null) {
            fillMooreNeighbourThirdRule(x, y, previousArray, currentArray, probability);
            return;
        }
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    static void fillMooreNeighbourFirstRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray, int probability) {
        List<Cell> grainsCount = new ArrayList<>();
        Cell c;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                addGrainToList(previousArray, grainsCount, i, j);
            }
        }
        if (grainsCount.isEmpty()) {
            fillMooreNeighbourSecondRule(x, y, previousArray, currentArray, probability);
            return;
        }
        c = fiveOrMoreOccurrences(grainsCount);
        if (c == null) {
            fillMooreNeighbourSecondRule(x, y, previousArray, currentArray, probability);
            return;
        }
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    public static Cell getMostCommonCell(List<Cell> grainsCount) {
        BidiMap<Cell, Integer> bidiMap = new TreeBidiMap<>(toFrequencyMap(grainsCount));
        return bidiMap.getKey(Collections.max(bidiMap.values()));
    }

    public static Cell fiveOrMoreOccurrences(List<Cell> grainsCount) {
        return getCellByOccurrences(grainsCount, 5);
    }

    public static Cell threeOrMoreOccurrences(List<Cell> grainsCount) {
        return getCellByOccurrences(grainsCount, 3);
    }

    private static Cell getCellByOccurrences(List<Cell> grainsCount, int occurrences) {
        BidiMap<Cell, Integer> bidiMap = new TreeBidiMap<>(toFrequencyMap(grainsCount));
        Integer key = Collections.max(bidiMap.values());
        if (key >= occurrences) {
            return bidiMap.getKey(key);
        }
        return null;
    }

    public static Map<Cell, Integer> toFrequencyMap(List<Cell> cells) {
        Map<Cell, Integer> cellIntegerMap = new HashMap<Cell, Integer>();
        cells.forEach(cell -> {
            if (!cellIntegerMap.containsKey(cell)) {
                cellIntegerMap.put(cell, 1);
            } else {
                cellIntegerMap.put(cell, cellIntegerMap.get(cell) + 1);
            }
        });
        return cellIntegerMap;
    }
}
