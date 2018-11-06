package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class MultiscaleModel {

    public static MultiscaleModel instance;

    private final int sizeX = 200;

    private final int sizeY = 200;

    private Cell[][] array = generateEmptyArray();

    private boolean grainsGenerated = false;

    private int numberOfGrains = 0;

    private boolean arrayFilled = false;

    private boolean probabilityAdded = false;

    private boolean boundariesAdded = false;

    private boolean grainsRemoved = false;

    //    private boolean inclusionAdded = false;

    private int probability = 90;

    private Structure structure = Structure.SUBSTRUCTURE;

    private boolean structureChoosen = false;

    private MultiscaleModel() {
    }

    public static MultiscaleModel getInstance() {
        if (instance == null) {
            instance = new MultiscaleModel();
        }
        return instance;
    }

    public void generateRandomGrains(int numberOfNucleons) {
        setNumberOfGrains(numberOfNucleons);
        if (!probabilityAdded) {
            setProbabilityAdded(true);
        }
        List<Cell> cells = generateRandomCells(numberOfNucleons);
        int x;
        int y;
        int i = numberOfNucleons - 1;
        Cell c;
        // for(int i = numberOfNucleons - 1; i >= 0; i--){
        while (!cells.isEmpty()) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if ((c = array[x][y]).getId() == 0 && !c.getState().equals(Cell.State.INCLUSION) && !c.getState().equals(Cell.State.PHASE)) {
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
//                    fillMooreNeighbour(i, j, previousArray, currentArray);
                    fillMooreNeighbourFirstRule(i, j, previousArray, currentArray);
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
                addGrainToList(previousArray, grainsCount, i, j);
            }
        }
        if (grainsCount.isEmpty()) {
            return;
        }

        c = MultiscaleModelHelper.getMostCommonCell(grainsCount);
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    private void fillMooreNeighbourFirstRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        List<Cell> grainsCount = new ArrayList<>();
        Cell c;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                addGrainToList(previousArray, grainsCount, i, j);
            }
        }
        if (grainsCount.isEmpty()) {
            fillMooreNeighbourSecondRule(x, y, previousArray, currentArray);
            return;
        }
        c = MultiscaleModelHelper.fiveOrMoreOccurrences(grainsCount);
        if (c == null) {
            fillMooreNeighbourSecondRule(x, y, previousArray, currentArray);
            return;
        }
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    private void fillMooreNeighbourSecondRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        List<Cell> grainsCount = new ArrayList<>();
        addGrainToList(previousArray, grainsCount, x - 1, y);
        addGrainToList(previousArray, grainsCount, x + 1, y);
        addGrainToList(previousArray, grainsCount, x, y - 1);
        addGrainToList(previousArray, grainsCount, x, y + 1);
        if (grainsCount.isEmpty()) {
            fillMooreNeighbourThirdRule(x, y, previousArray, currentArray);
            return;
        }
        Cell c = MultiscaleModelHelper.threeOrMoreOccurrences(grainsCount);
        if (c == null) {
            fillMooreNeighbourThirdRule(x, y, previousArray, currentArray);
            return;
        }
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    private void fillMooreNeighbourThirdRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        List<Cell> grainsCount = new ArrayList<>();
        addGrainToList(previousArray, grainsCount, x - 1, y - 1);
        addGrainToList(previousArray, grainsCount, x + 1, y + 1);
        addGrainToList(previousArray, grainsCount, x - 1, y + 1);
        addGrainToList(previousArray, grainsCount, x + 1, y - 1);
        if (grainsCount.isEmpty()) {
            fillMooreNeighbourFourthRule(x, y, previousArray, currentArray);
            return;
        }
        Cell c = MultiscaleModelHelper.threeOrMoreOccurrences(grainsCount);
        if (c == null) {
            fillMooreNeighbourFourthRule(x, y, previousArray, currentArray);
            return;
        }
        currentArray[x][y].setId(c.getId());
        currentArray[x][y].setRgb(c.getRgb());
    }

    private void fillMooreNeighbourFourthRule(int x, int y, Cell[][] previousArray, Cell[][] currentArray) {
        int r = new Random().nextInt(100);
        if (r < probability) {
            fillMooreNeighbour(x, y, previousArray, currentArray);
        }
    }

    public void chooseStructure(Structure structure, int numberOfGrainsToStay) {
        this.structure = structure;
        this.structureChoosen = true;
        if (numberOfGrainsToStay >= numberOfGrains) {
            return;
        }
        int x, y;
        Cell c;
        int numberOfGrainsToRemove = numberOfGrains - numberOfGrainsToStay - 1;
        while (numberOfGrainsToRemove >= 0) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if ((c = array[x][y]).getId() > 0) {
                removeGrainFromArray(c);
                numberOfGrainsToRemove--;
            }
        }
        addStructureToGrains(structure);
        setArrayFilled(false);
        setGrainsGenerated(false);
    }

    private void removeGrainFromArray(Cell c) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (array[i][j].isTheSameAs(c)) {
                    array[i][j] = new Cell(0);
                }
            }
        }
    }

    private void addStructureToGrains(Structure structure) {
        Cell c;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if ((c = array[i][j]).getId() > 0) {
                    if (structure.equals(Structure.DUAL_PHASE)) {
                        array[i][j] = new Cell(Cell.State.PHASE);
                    } else {
                        c.setId(-3);
                        array[i][j] = c;
                    }
                }
            }
        }
    }

    private void addGrainToList(Cell[][] previousArray, List<Cell> grainsCount, int i, int j) {
        Cell c;
        if (i < 0 || i >= sizeX || j < 0 || j >= sizeY) {
            return;
        }
        if ((c = previousArray[i][j]).getId() != 0 && c.getId() != -1 && c.getId() != -2 && c.getId() != -3) {
            grainsCount.add(c);
        }
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
        setArray(generateEmptyArray());
        setGrainsGenerated(false);
        setArrayFilled(false);
        setProbabilityAdded(false);
        setStructure(Structure.SUBSTRUCTURE);
        setStructureChoosen(false);
        setBoundariesAdded(false);
        setGrainsRemoved(false);
//        inclusionAdded = false;
    }

    public void generateInclusions(int numberOfInclusions, float sizeOfInclusions, Shape shapeOfInclusions) {
//        if (inclusionAdded) {
//            return;
//        }
        if (shapeOfInclusions.equals(Shape.CIRCLE)) {
            addCircleInclusions(numberOfInclusions, sizeOfInclusions);
        } else {
            addSquareInclusions(numberOfInclusions, sizeOfInclusions);
        }
//        inclusionAdded = true;
    }

    private void addCircleInclusions(int numberOfInclusions, float sizeOfInclusions) {
        if (arrayFilled) {
            addCircleInclusionsForFilledArray(numberOfInclusions, (int) sizeOfInclusions, addGrainsBordersToEmptyArray());
        }
        if (!grainsGenerated && !arrayFilled) {
            addCircleInclusionsForEmptyArray(numberOfInclusions, (int) sizeOfInclusions);
        }
    }

    private void addCircleInclusionsForEmptyArray(int numberOfInclusions, int r) {
        int x, y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(r);
            y = getY(r);
            while (array[x][y].getState().equals(Cell.State.INCLUSION)) {
                x = getX(r);
                y = getY(r);
            }
            fillCircle(r, x, y);
        }
    }

    private void addCircleInclusionsForFilledArray(int numberOfInclusions, int r, Cell[][] cells) {
        int x;
        int y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(r);
            y = getY(r);
            while (array[x][y].getState().equals(Cell.State.INCLUSION) || array[x][y].getState().equals(Cell.State.PHASE) || !isGrainsBorder(x, y, array[x][y].getId())) {
                x = getX(r);
                y = getY(r);
            }
            fillCircle(r, x, y);
        }
    }

    private void addSquareInclusions(int numberOfInclusions, float sizeOfInclusions) {
        int a = (int) (sizeOfInclusions / Math.sqrt(2));
        if (arrayFilled) {
            addSquareInclusionsForFilledArray(numberOfInclusions, a, addGrainsBordersToEmptyArray());
        }
        if (!grainsGenerated && !arrayFilled) {
            addSquareInclusionsForEmptyArray(numberOfInclusions, a);
        }
    }

    private void addSquareInclusionsForFilledArray(int numberOfInclusions, int a, Cell[][] borders) {
        int x;
        int y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(a);
            y = getY(a);
            while (array[x][y].getState().equals(Cell.State.INCLUSION) || array[x][y].getState().equals(Cell.State.PHASE) || !isGrainsBorder(x, y, array[x][y].getId())) {
                x = getX(a);
                y = getY(a);
            }
            fillSquare(a, x, y);
        }
    }

    private void addSquareInclusionsForEmptyArray(int numberOfInclusions, int a) {
        int x, y;
        for (int i = 0; i < numberOfInclusions; i++) {
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

    private void fillCircle(int r, int x, int y) {
        double pi = Math.PI;
        int a, b;
        for (double i = 0; i <= 360; i += 0.01) {
            for (int j = r; j >= 0; j--) {
                a = (int) (j * Math.cos(i * pi / 2)) + x;
                b = (int) (j * Math.sin(i * pi / 2)) + y;
                if (a < 0 || a >= sizeX || b < 0 || b > sizeY) {
                    continue;
                }
                array[a][b] = new Cell(Cell.State.INCLUSION);
            }
        }
    }

    private int getY(int a) {
        return (int) (Math.floor(Math.random() * (sizeY - 2 - a)) + 1);
    }

    private int getX(int a) {
        return (int) (Math.floor(Math.random() * (sizeX - 2 - a)) + 1);
    }

    private boolean isGrainsBorder(int x, int y, int id) {
        for (int i = x; i <= x + 2; i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            if (array[i][y].getId() != id) {
                return true;
            }
        }
        for (int i = y; i <= y + 2; i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            if (array[x][i].getId() != id) {
                return false;
            }
        }
        return false;
    }

    private void removeIfNotGrainsBorder(int x, int y){
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        Cell c = array[x][y];
        if(c.getState().equals(Cell.State.BORDER)){
            return;
        }
        c = new Cell(0);
        c.setRgb(new int[]{255, 255, 255});
        array[x][y] = c;
    }

    private void fillIfGrainsBorder(int x, int y, int id, Cell[][] borderCells) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            if (array[i][y].getId() != id) {
                borderCells[x][y] = new Cell(Cell.State.BORDER);
            }
        }
        for (int i = y - 1; i <= y + 1; i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            if (array[x][i].getId() != id) {
                borderCells[x][y] = new Cell(Cell.State.BORDER);
            }
        }
    }

    public void addBoundaries() {
        array = addGrainsBorders();
        setBoundariesAdded(true);
    }

    public void removeGrains() {
        removeGrainsLeaveBorders();
        setGrainsRemoved(true);
    }

    private void removeGrainsLeaveBorders(){
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                removeIfNotGrainsBorder(i, j);
            }
        }
    }

    private Cell[][] addGrainsBordersToEmptyArray() {
        Cell[][] borderCells = generateEmptyArray();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells);
            }
        }
        return borderCells;
    }

    private Cell[][] addGrainsBorders() {
        Cell[][] borderCells = cloneArray();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells);
            }
        }
        return borderCells;
    }

    private Cell[][] cloneArray(){
        Cell[][] clone = new Cell[sizeX][sizeY];
        for(int i = 0; i < sizeX; i++){
            for(int j = 0; j < sizeY; j++){
                clone[i][j] = array[i][j];
            }
        }
        return clone;
    }
}