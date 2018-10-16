package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class MultiscaleModel {

    public static MultiscaleModel instance;

    @Getter
    private final int sizeX = 100;
    @Getter
    private final int sizeY = 100;
    @Getter
    private Cell[][] array = generateEmptyArray();
    @Getter
    private boolean grainsGenerated = false;
    @Getter
    private boolean arrayFilled = false;

    private MultiscaleModel() {
    }

    public static MultiscaleModel getInstance() {
        if (instance == null) {
            instance = new MultiscaleModel();
        }
        return instance;
    }

    public void generateRandomGrains(int numberOfNucleons) {
        List<Cell> cells = generateRandomCells(numberOfNucleons);
        int x;
        int y;
        int i = numberOfNucleons - 1;
        // for(int i = numberOfNucleons - 1; i >= 0; i--){
        while (!cells.isEmpty()) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if (array[x][y].getId() == 0) {
                array[x][y] = cells.remove(i--);
            }
        }
        this.grainsGenerated = true;
    }

    private Cell[][] generateEmptyArray() {
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

    public void growGrainsLoop(int numberOfIterations) {
        if (numberOfIterations >= 50) {
            while (countEmptyCells() != 0) {
                growGrains();
            }
            arrayFilled = true;
            return;
        }
        for (int i = 0; i < numberOfIterations; i++) {
            growGrains();
        }
        if (countEmptyCells() == 0) {
            arrayFilled = true;
        }
    }

    private int countEmptyCells() {
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

    public void growGrains() {
        Cell[][] previousArray = array;
        Cell[][] currentArray = generateEmptyArray();
        for (int i = 1; i < sizeX - 1; i++) {
            for (int j = 1; j < sizeY - 1; j++) {
                if (previousArray[i][j].getId() != 0) {
                    mooreNeighbourhood(i, j, previousArray, currentArray);
                }
            }
        }
        array = currentArray;
    }

    private void mooreNeighbourhood(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        Cell c = previousArray[x][y];
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((i < 0 || i > sizeX) || (j < 0 || j > sizeY)) {
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


    private void fillMooreNeighbour(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        if ((x == 0 || x == sizeX - 1) || (y == 0 || y == sizeY - 1)) {
            return;
        }
//        array[x][y] = generateRandomCell(x, y);
        List<Cell> grainsCount = new ArrayList<>();
        Cell c;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((c = previousArray[i][j]).getId() != 0) {
                    grainsCount.add(c);
                }
            }
        }
        System.out.println(grainsCount);
        c = mostCommon(grainsCount);
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    private Cell mostCommon(List<Cell> grainsCount) {
        BidiMap<Cell, Integer> bidiMap = new TreeBidiMap<>(toFrequencyMap(grainsCount));
        return bidiMap.getKey(Collections.max(bidiMap.values()));
    }

    static private Map<Cell, Integer> toFrequencyMap(List<Cell> cells) {
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

    public static void main(String[] args) {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(1));
        cells.add(new Cell(1));
        cells.add(new Cell(2));
        cells.add(new Cell(2));
        cells.add(new Cell(3));
        System.out.println(toFrequencyMap(cells));
        BidiMap<Cell, Integer> bidiMap = new TreeBidiMap<>(toFrequencyMap(cells));
        System.out.println(Collections.max(bidiMap.values()));
    }

    private Cell generateRandomCell(int x, int y) {
        Cell cell = new Cell(Integer.valueOf(String.valueOf(x) + String.valueOf(y)));
        cell.setRgb(generateRandomColor());
        return cell;
    }

    private List<Cell> generateRandomCells(int numberOfNucleons) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 1; i <= numberOfNucleons; i++) {
            Cell c = new Cell(i);
            c.setRgb(generateRandomColor());
            cells.add(c);
        }
        return cells;
    }

    private int[] generateRandomColor() {
        int[] color = new int[3];
        color[0] = (int) (Math.floor(Math.random() * 255) + 1);
        color[1] = 0;//(int) (Math.floor(Math.random() * 255) + 1);
        color[2] = (int) (Math.floor(Math.random() * 255) + 1);
        return color;
    }

    public void clear() {
        array = generateEmptyArray();
        grainsGenerated = false;
    }

    public void printArray() {
        List<Cell[]> list = new ArrayList(Arrays.asList(array));
        list.forEach(cells -> System.out.println(Arrays.asList(cells)));
    }

    public InputStream export(Type type) throws FileNotFoundException {
        if (type.equals(Type.DATA_FILE)) {
            return null;
        }
        return null;
    }

    public String getDataFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("x\ty\tid\tr\tg\tb\n");
        Cell cell;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                cell = array[i][j];
                sb.append(i + "\t" + j + "\t" + cell.getId() + "\t" + cell.getRgb()[0] + "\t" + cell.getRgb()[1] + "\t" + cell.getRgb()[2] + "\n");
            }
        }
        return sb.toString();
    }

    public File getBitmap() throws IOException {
        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
        Cell cell;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                cell = array[i][j];
//                image.setRGB(i, j, ((cell.getRgb()[0]&0x0ff)<<16)|((cell.getRgb()[1]&0x0ff)<<8)|(cell.getRgb()[2]&0x0ff));
                image.setRGB(i, j, (cell.getRgb()[0] << 16 | cell.getRgb()[1] << 8 | cell.getRgb()[2]));
            }
        }
        File file = new File("bitmap.png");
        ImageIO.write(image, "png", file);
        return file;
    }
}
