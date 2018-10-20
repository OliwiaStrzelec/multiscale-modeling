package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

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
    @Getter
    private boolean inclusionAdded = false;

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
        Cell c;
        // for(int i = numberOfNucleons - 1; i >= 0; i--){
        while (!cells.isEmpty()) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if ((c = array[x][y]).getId() == 0 && !c.getState().equals(Cell.State.INCLUSION)) {
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
        int k = 0;
        Cell[][] previousArray = array;
        Cell[][] currentArray = generateEmptyArray();
        Cell c;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
//                if (previousArray[i][j].getId() != 0) {
//                    mooreNeighbourhood(i, j, previousArray, currentArray);
//                }
                if ((c = previousArray[i][j]).getId() == 0) {
                    fillMooreNeighbour(i, j, previousArray, currentArray);
                } else {
                    currentArray[i][j].setId(c.getId());
                    currentArray[i][j].setRgb(c.getRgb());
                    currentArray[i][j].setState(c.getState());
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
        List<Cell> grainsCount = new ArrayList<>();
        Cell c;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i < 0 || i >= sizeX || j < 0 || j >= sizeY) {
                    continue;
                }
                if ((c = previousArray[i][j]).getId() != 0 && c.getId() != -1) {
                    grainsCount.add(c);
                }
            }
        }
        if (grainsCount.isEmpty()) {
            return;
        }

        c = MultiscaleModelHelper.mostCommon(grainsCount);
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    public static void main(String[] args) {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(1));
        cells.add(new Cell(1));
        cells.add(new Cell(2));
        cells.add(new Cell(2));
        cells.add(new Cell(3));
        System.out.println(MultiscaleModelHelper.toFrequencyMap(cells));
        BidiMap<Cell, Integer> bidiMap = new TreeBidiMap<>(MultiscaleModelHelper.toFrequencyMap(cells));
        System.out.println(Collections.max(bidiMap.values()));
    }

    private Cell generateRandomCell(int x, int y) {
        Cell cell = new Cell(Integer.valueOf(String.valueOf(x) + String.valueOf(y)));
        cell.setRgb(MultiscaleModelHelper.generateRandomColor());
        return cell;
    }

    private List<Cell> generateRandomCells(int numberOfNucleons) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 1; i <= numberOfNucleons; i++) {
            Cell c = new Cell(i);
            c.setRgb(MultiscaleModelHelper.generateRandomColor());
            cells.add(c);
        }
        return cells;
    }

    public void clear() {
        array = generateEmptyArray();
        grainsGenerated = false;
        arrayFilled = false;
        inclusionAdded = false;
    }

    public void generateInclusions(int numberOfInclusions, float sizeOfInclusions, Shape shapeOfInclusions) {
        if (inclusionAdded) {
            return;
        }
        if (shapeOfInclusions.equals(Shape.CIRCLE)) {
            addCircleInclusions(numberOfInclusions, sizeOfInclusions);
        } else {
            addSquareInclusions(numberOfInclusions, sizeOfInclusions);
        }
        inclusionAdded = true;
    }

    private void addSquareInclusions(int numberOfInclusions, float sizeOfInclusions) {
        int a = (int) (sizeOfInclusions / Math.sqrt(2));
        if(arrayFilled){
            addSquareInclusionsForFilledArray(numberOfInclusions, a, detectBorders());
        }
        if(!grainsGenerated && !arrayFilled){
            addSquareInclusionsForEmptyArray(numberOfInclusions, a);
        }
    }


    private void addSquareInclusionsForFilledArray(int numberOfInclusions, int a, Cell[][] borders) {
        int x;
        int y;
        for(int i = 0; i < numberOfInclusions; i++){
            x = getX(a);
            y = getY(a);
            while (array[x][y].getState().equals(Cell.State.INCLUSION) || !isGrainsBorder(x, y, array[x][y].getId())) {
                x = getX(a);
                y = getY(a);
            }
            fillSquare(a, x, y);
        }
    }

    private void addSquareInclusionsForEmptyArray(int numberOfInclusions, int a) {
        int x, y;
        for(int i = 0; i < numberOfInclusions; i++){
            x = getX(a);
            y = getY(a);
            while (array[x][y].getState().equals(Cell.State.INCLUSION)) {
                x = getX(a);
                y = getY(a);
            }
            fillSquare((int) a, x, y);
        }
    }

    private void fillSquare(int a, int x, int y) {
        for (int i = x; i <= x + a && i < sizeX; i++) {
            for (int j = y; j <= y + a && j < sizeY; j++) {
                array[i][j] = new Cell(Cell.State.INCLUSION);
            }
        }
    }

    private int getY(int a) {
        return (int) (Math.floor(Math.random() * (sizeY - 2 - a)) + 1);
    }

    private int getX(int a) {
        return (int) (Math.floor(Math.random() * (sizeX - 2 - a)) + 1);
    }

    private boolean isGrainsBorder(int x, int y, int id){
        for(int i = x; i <= x + 2; i++){
            if(i < 0 || i >= sizeX){
                continue;
            }
            if(array[i][y].getId() != id){
                return true;
            }
        }
        for(int i = y; i <= y + 2; i++){
            if(i < 0 || i >= sizeY){
                continue;
            }
            if(array[x][i].getId() != id){
                return false;
            }
        }
        return false;
    }


    private void fillIfGrainsBorder(int x, int y, int id, Cell[][] borderCells) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        for(int i = x - 1; i <= x + 1; i++){
            if(i < 0 || i >= sizeX){
                continue;
            }
            if(array[i][y].getId() != id){
                borderCells[x][y] = new Cell(Cell.State.BORDER);
            }
        }
        for(int i = y - 1; i <= y + 1; i++){
            if(i < 0 || i >= sizeY){
                continue;
            }
            if(array[x][i].getId() != id){
                borderCells[x][y] = new Cell(Cell.State.BORDER);
            }
        }
    }


    private Cell[][] detectBorders() {
        Cell[][] borderCells = generateEmptyArray();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells);
            }
        }
        return borderCells;
    }

    private void addCircleInclusions(int numberOfInclusions, float sizeOfInclusions) {

    }


}