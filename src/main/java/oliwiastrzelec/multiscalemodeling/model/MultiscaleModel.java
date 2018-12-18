package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

@Getter
@Setter
public class MultiscaleModel {

    public static MultiscaleModel instance;

    private final int sizeX = 200;

    private final int sizeY = 200;

    private int numberOfRecrystalizedNucleons = 0;

    private Cell[][] array = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);

    private Cell[][] energyArray = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);

    private boolean grainsGenerated = false;

    private int numberOfGrains = 0;

    private boolean arrayFilled = false;

    private boolean probabilityAdded = false;

    private boolean boundaryEnergyAdded = false;

    private boolean boundariesAdded = false;

    private boolean energyDistributionAdded = false;

    private boolean energyArrayFilled = false;

    private boolean energyView = false;

    private boolean growingAfterBoundaries = false;

    private boolean grainsRemoved = false;

    private boolean isMonteCarlo = false;

    private int probability = 90;

    private double boundaryEnergy = 0.5;

    private Structure structure = Structure.SUBSTRUCTURE;

    private boolean structureChoosen = false;

    private static Random random = new Random(50);

    private MultiscaleModel() {
    }

    public static MultiscaleModel getInstance() {
        if (instance == null) {
            instance = new MultiscaleModel();
        }
        return instance;
    }

    public void generateRandomGrains(Mechanism mechanism, int numberOfNucleons) {
        setNumberOfGrains(numberOfNucleons);
        if (mechanism.equals(Mechanism.MONTE_CARLO)) {
            setMonteCarlo(true);
            generateMonteCarloRandomGrains(numberOfNucleons);
            return;
        }
        if (!probabilityAdded) {
            setProbabilityAdded(true);
        }
        List<Cell> cells = MultiscaleModelHelper.generateRandomCells(numberOfNucleons);
        int x;
        int y;
        int i = numberOfNucleons - 1;
        Cell c;
        // for(int i = numberOfNucleons - 1; i >= 0; i--){
        while (!cells.isEmpty()) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if ((c = array[x][y]).getId() == 0 && !c.getState().equals(Cell.State.INCLUSION) && !c.getState().equals(Cell.State.PHASE) && !c.getState().equals(Cell.State.INSIDE_BORDER)) {
                array[x][y] = cells.remove(i--);
            }
        }
        setGrainsGenerated(true);
    }

    private void generateMonteCarloRandomGrains(int numberOfNucleons) {
        List<Cell> cells = MultiscaleModelHelper.generateRandomCells(numberOfNucleons);
        int id;
        Cell cell;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j].getId() != 0 || array[i][j].getState().equals(Cell.State.INCLUSION) || array[i][j].getState().equals(Cell.State.PHASE)) {
                    continue;
                }
                id = random.nextInt(numberOfNucleons);
                cell = cells.get(id);
                array[i][j].setRgb(cell.getRgb());
                array[i][j].setState(cell.getState());
                array[i][j].setId(cell.getId());
            }
        }

        setGrainsGenerated(true);

    }

    public void growGrainsLoop(int numberOfIterations) {
        if (energyDistributionAdded) {
            recrystalizationLoop(numberOfIterations);
            return;
        }
        if (isMonteCarlo) {
            growMonteCarloGrainsLoop(numberOfIterations);
            return;
        }
        int k = 0;
        if (numberOfIterations >= 50 && !growingAfterBoundaries) {
            while (MultiscaleModelHelper.countEmptyCells(array) != 0 && k < 100) {
                growGrains();
                k++;
            }
            setArrayFilled(true);
            return;
        }
        for (int i = 0; i < numberOfIterations; i++) {
            growGrains();
        }
        if (MultiscaleModelHelper.countEmptyCells(array) == 0) {
            setArrayFilled(true);
        }
    }

    private void recrystalizationLoop(int numberOfIterations) {
        for (int i = 0; i < numberOfIterations; i++) {
            recrystalization();
            updateEnergyArray();
        }
    }

    private void growMonteCarloGrainsLoop(int numberOfIterations) {
        setBoundaryEnergyAdded(true);
        for (int i = 0; i < numberOfIterations; i++) {
            growMonteCarloGrains();
        }
    }

    private void recrystalization() {
        List<CellWithCoordinates> cells = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j].getId() != -5) {
                    cells.add(new CellWithCoordinates(array[i][j], i, j));
                }
            }
        }
        int id;
        double energyBefore, energyAfter;
        Cell cell;
        CellWithCoordinates cellWithCoordinates, recrystalizedNeighbour;
        while (!cells.isEmpty()) {
            id = random.nextInt(cells.size());
            cellWithCoordinates = cells.remove(id);
            if ((recrystalizedNeighbour = getRecrystalizedNeighbour(cellWithCoordinates.getX(), cellWithCoordinates.getY())) == null) {
                continue;
            }
            System.out.println("recrystalization");
            if (cellWithCoordinates.getId() != -5) {
                energyBefore = countEnergy(cellWithCoordinates.getX(), cellWithCoordinates.getY(), cellWithCoordinates.getId()) + array[cellWithCoordinates.getY()][cellWithCoordinates.getY()].getEnergy();
                energyAfter = countEnergy(cellWithCoordinates.getX(), cellWithCoordinates.getY(), -5);
                System.out.println(energyBefore + " " + energyAfter);
                if (energyAfter - energyBefore < 0) {
                    array[cellWithCoordinates.getX()][cellWithCoordinates.getY()] = recrystalizedNeighbour;
                }
            }
        }
    }

    private CellWithCoordinates getRecrystalizedNeighbour(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i < 0 || i >= array.length || j < 0 || j >= array[0].length) {
                    continue;
                }
                if (array[i][j].getState().equals(Cell.State.RECRYSTALIZED)) {
                    return new CellWithCoordinates(array[i][j], i, j);
                }
            }
        }
        return null;
    }

    private void growMonteCarloGrains() {
        List<CellWithCoordinates> cells = addCellsWithCoordinatesToList(array);
        int id;
//        double energyBefore, energyAfter;
//        Cell cell;
        CellWithCoordinates cellWithCoordinates;
        while (!cells.isEmpty()) {
            id = random.nextInt(cells.size());
            cellWithCoordinates = cells.remove(id);
            growMonteCarlo(cellWithCoordinates.getX(), cellWithCoordinates.getY());
        }
    }

    private void growMonteCarlo(int x, int y) {
        double energyBefore, energyAfter;
        List<Cell> neighboursBefore, neighboursAfter, neighbours;
        Cell cell = array[x][y], neighbour;
        if (array[x][y].getId() < 0 || array[x][y].getState().equals(Cell.State.INCLUSION) || array[x][y].getState().equals(Cell.State.PHASE)) {
            return;
        }
        neighboursBefore = getNeighbours(x, y, array[x][y].getId());
        if(neighboursBefore.size() == 0){
            return;
        }
        energyBefore = neighboursBefore.size() * boundaryEnergy;
        neighbours = getDistinctNeighbours(neighboursBefore);
        Cell newCell = neighbours.get(random.nextInt(neighbours.size()));
        neighboursAfter = getNeighbours(x, y, newCell.getId());
        energyAfter = neighboursAfter.size() * boundaryEnergy;

//            energyBefore = countEnergy(x, y, cell.getId());
//            neighbour = getRandomNeighbour(x, y, cell.getId());
//            if (neighbour == null) {
//                return;
//            }
//            energyAfter = countEnergy(x, y, neighbour.getId());
            if ((energyAfter - energyBefore) <= 0) {
                array[x][y].setId(newCell.getId());
                array[x][y].setRgb(newCell.getRgb());
                array[x][y].setState(newCell.getState());
                array[x][y].setEnergy(newCell.getEnergy());
            }
    }

    private List<Cell> getDistinctNeighbours(List<Cell> neighboursBefore) {
        Set<Cell> cellsSet = new HashSet<>();
        List<Cell> cells = new ArrayList<>();
        neighboursBefore.forEach(cell -> cellsSet.add(cell));
        cellsSet.forEach(cell -> cells.add(cell));
        return cells;
    }

    private List<Cell> getNeighbours(int x, int y, int id) {
        List<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= array.length || i < 0 || j >= array[0].length || j < 0) {
                    continue;
                }
                if(i == x && j == y){
                    continue;
                }
                if (array[i][j].getId() != id && !(array[i][j].getId() < 0) && !array[i][j].getState().equals(Cell.State.INCLUSION) && !array[i][j].getState().equals(Cell.State.PHASE)) {
                    cells.add(array[i][j]);
                }
            }
        }
        return cells;
    }

    private Cell getRandomNeighbour(int x, int y, int id) {
        List<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= array.length || i < 0 || j >= array[0].length || j < 0) {
                    continue;
                }
//                if(i == x && j == y){
//                    continue;
//                }
                if (array[i][j].getId() != id && !(array[i][j].getId() < 0) && !array[i][j].getState().equals(Cell.State.INCLUSION) && !array[i][j].getState().equals(Cell.State.PHASE)) {
                    cells.add(array[i][j]);
                }
            }
        }
        if (cells.isEmpty()) {
            return null;
        }
        return cells.get(random.nextInt(cells.size()));
    }

    private double countEnergy(int x, int y, int id) {
        int numberOfDifferentNeighbours = 0;
        for (int i = x - 1; i < x + 1; i++) {
            for (int j = y - 1; j < y + 1; j++) {
                if (i >= array.length || i < 0 || j >= array[0].length || j < 0) {
                    continue;
                }
//                if(i == x && j == y){
//                    continue;
//                }
                if (array[i][j].getId() != id && array[i][j].getId() > 0 && !array[i][j].getState().equals(Cell.State.INCLUSION) && !array[i][j].getState().equals(Cell.State.PHASE)) {
                    numberOfDifferentNeighbours++;
                }
            }
        }
        return boundaryEnergy * numberOfDifferentNeighbours;
    }

    private List<CellWithCoordinates> addCellsWithCoordinatesToList(Cell[][] array) {
        List<CellWithCoordinates> cells = new ArrayList<>();
        CellWithCoordinates cellWithCoordinates;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                cellWithCoordinates = new CellWithCoordinates(array[i][j], i, j);
                cells.add(cellWithCoordinates);
            }
        }
        return cells;
    }

    public void stopGrowing() {
        setArrayFilled(true);
        //setGrowingAfterBoundaries(false);
    }

    public void growGrains() {
        int k = 0;
        Cell[][] previousArray = array;
        Cell[][] currentArray = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);
        Cell c;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if ((c = previousArray[i][j]).getId() == 0) {
                    MooreNeighbourhood.fillMooreNeighbourFirstRule(i, j, previousArray, currentArray, probability);
                } else {
                    currentArray[i][j].setId(c.getId());
                    currentArray[i][j].setRgb(c.getRgb());
                    currentArray[i][j].setState(c.getState());
                }
            }
        }
        array = currentArray;
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
                Substructure.removeGrainFromArray(c, array);
                numberOfGrainsToRemove--;
            }
        }
        Substructure.addStructureToGrains(structure, array);
        setArrayFilled(false);
        setGrainsGenerated(false);
        setMonteCarlo(false);
    }

    public void clear() {
        setArray(MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY));
        setGrainsGenerated(false);
        setArrayFilled(false);
        setProbabilityAdded(false);
        setStructure(Structure.SUBSTRUCTURE);
        setStructureChoosen(false);
        setBoundariesAdded(false);
        setGrowingAfterBoundaries(false);
        setMonteCarlo(false);
        setBoundaryEnergyAdded(false);
        setEnergyView(false);
        setEnergyArrayFilled(false);
        setEnergyDistributionAdded(false);
        setNumberOfRecrystalizedNucleons(0);
    }

    public void generateInclusions(int numberOfInclusions, float sizeOfInclusions, Shape shapeOfInclusions) {
        if (shapeOfInclusions.equals(Shape.CIRCLE)) {
            addCircleInclusions(numberOfInclusions, sizeOfInclusions, array);
        } else {
            addSquareInclusions(numberOfInclusions, sizeOfInclusions);
        }
    }

    private void addCircleInclusions(int numberOfInclusions, float sizeOfInclusions, Cell[][] array) {
        if (arrayFilled) {
            Inclusions.addCircleInclusionsForFilledArray(numberOfInclusions, (int) sizeOfInclusions, array);
        }
        if (!grainsGenerated && !arrayFilled) {
            Inclusions.addCircleInclusionsForEmptyArray(numberOfInclusions, (int) sizeOfInclusions, array);
        }
    }

    private void addSquareInclusions(int numberOfInclusions, float sizeOfInclusions) {
        int a = (int) (sizeOfInclusions / Math.sqrt(2));
        if (arrayFilled) {
            Inclusions.addSquareInclusionsForFilledArray(numberOfInclusions, a, array);
        }
        if (!grainsGenerated && !arrayFilled) {
            Inclusions.addSquareInclusionsForEmptyArray(numberOfInclusions, a, array);
        }
    }

    private void removeIfNotGrainsBorder(int x, int y) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        Cell c = array[x][y];
        if (c.getState().equals(Cell.State.INSIDE_BORDER)) {
            array[x][y] = new Cell(Cell.State.INSIDE_BORDER);
            return;
        }
        if (c.getState().equals(Cell.State.BORDER)) {
            array[x][y] = new Cell(Cell.State.INCLUSION);
            return;
        }
        c = new Cell(0);
        c.setRgb(new int[]{255, 255, 255});
        array[x][y] = c;
    }

    private void fillIfGrainsBorder(int x, int y, int id, Cell[][] borderCells, int boundarySize) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            if (array[i][y].getId() != id) {
                borderCells[x][y] = new Cell(Cell.State.BORDER);
                fillBorderXDimension(x, y, borderCells, boundarySize);
            }
        }
        for (int i = y - 1; i <= y + 1; i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            if (array[x][i].getId() != id) {
                borderCells[x][y] = new Cell(Cell.State.BORDER);
                fillBorderYDimension(x, y, borderCells, boundarySize);
            }
        }
    }

    private void fillBorderXDimension(int x, int y, Cell[][] cells, int boundarySize) {
        for (int i = x - (boundarySize - 1); i <= x + (boundarySize - 1); i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            cells[i][y] = new Cell(Cell.State.BORDER);
        }
    }

    private void fillBorderYDimension(int x, int y, Cell[][] cells, int boundarySize) {
        for (int i = y - (boundarySize - 1); i <= y + (boundarySize - 1); i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            cells[x][i] = new Cell(Cell.State.BORDER);
        }
    }

    public void addBoundaries(int boundarySize, int numberOfGrains) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        if (numberOfGrains == 0) {
            array = addGrainsBorders(boundarySize);
        } else {
            array = addBordersToGrains(boundarySize, numberOfGrains, array);
        }
        setBoundariesAdded(true);
    }

    public Cell[][] addBordersToGrains(int boundarySize, int numberOfGrains, Cell[][] array) {
        Cell[][] cellBorders = MultiscaleModelHelper.cloneArray(array);
        int x, y;
        Cell c;
        while (numberOfGrains > 0) {
            x = (int) (Math.floor(Math.random() * (array.length - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (array[0].length - 2)) + 1);
            if ((c = array[x][y]).getId() > 0) {
                addBorderToGrain(c, cellBorders, boundarySize);
                numberOfGrains--;
            }
        }
        return cellBorders;
    }

    private void addBorderToGrain(Cell c, Cell[][] cellBorders, int boundarySize) {
        List<CellWithCoordinates> cells = allCells(c.getId(), cellBorders);
        cells.forEach(cell -> {
            fillIfGrainsBorder(cell.getX(), cell.getY(), c.getId(), cellBorders, boundarySize);
        });
        cells.forEach(cell -> {
            changeStateIfNotGrainsBorder(cell.getX(), cell.getY(), cellBorders);
        });
    }

    private void changeStateIfNotGrainsBorder(int x, int y, Cell[][] cellBorders) {
        Cell c;
        if (!((c = cellBorders[x][y]).getState().equals(Cell.State.BORDER))) {
            c.setState(Cell.State.INSIDE_BORDER);
            cellBorders[x][y] = c;
        }
    }

    private List<CellWithCoordinates> allCells(int id, Cell[][] cellBorders) {
        List<CellWithCoordinates> cells = new ArrayList<>();
        Cell c;
        for (int i = 0; i < cellBorders.length; i++) {
            for (int j = 0; j < cellBorders[0].length; j++) {
                if ((c = cellBorders[i][j]).getId() == id) {
                    cells.add(new CellWithCoordinates(c, i, j));
                }
            }
        }
        return cells;
    }

    public void removeGrains() {
        removeGrainsLeaveBorders();
        setGrainsGenerated(false);
        setBoundariesAdded(false);
        setGrowingAfterBoundaries(true);
        setArrayFilled(false);
    }

    private void removeGrainsLeaveBorders() {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                removeIfNotGrainsBorder(i, j);
            }
        }
    }

    private Cell[][] addGrainsBordersToEmptyArray() {
        Cell[][] borderCells = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells, 1);
            }
        }
        return borderCells;
    }

    private Cell[][] addGrainsBorders(int boundarySize) {
        Cell[][] borderCells = cloneArray();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells, boundarySize);
            }
        }
        return borderCells;
    }

    private Cell[][] cloneArray() {
        return MultiscaleModelHelper.cloneArray(array);
    }

    public void countEnergy(EnergyDistribution energyDistribution, int firstEnergyValue, int secondEnergyValue) {
        setEnergyDistributionAdded(true);
        if (energyDistribution.equals(EnergyDistribution.HOMOGENOUS)) {
            countEnergyHomogenous(firstEnergyValue);
        } else {
            countEnergyHeterogenous(firstEnergyValue, secondEnergyValue);
        }
        fillEnergyArray();
        setEnergyArrayFilled(true);
    }

    private void fillEnergyArray() {
        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[0].length; j++) {
                energyArray[i][j].setRgb(new int[]{200 * array[i][j].getEnergy() / 15 + 55, 0, 0});
            }
        }
        return;
    }

    private void countEnergyHeterogenous(int firstEnergyValue, int secondEnergyValue) {
        int min, max;
        if (firstEnergyValue != secondEnergyValue) {
            min = min(firstEnergyValue, secondEnergyValue);
            max = max(firstEnergyValue, secondEnergyValue);
        } else {
            min = firstEnergyValue;
            max = firstEnergyValue + 5;
        }
        countEnergyHomogenous(min);
        countEnergyIfGrainBorder(max);
    }

    private void countEnergyIfGrainsBorder(int energy, int x, int y, Cell[][] borderCells, int boundarySize) {
        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            if (array[i][y].getId() != array[x][y].getId()) {
                borderCells[x][y] = array[i][y];
                borderCells[x][y].setEnergy(energy);
//                countEnergyXDimension(x, y, borderCells, boundarySize);
            }
        }
        for (int i = y - 1; i <= y + 1; i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            if (array[x][i].getId() != array[x][y].getId()) {
                borderCells[x][y] = array[x][i];
                borderCells[x][y].setEnergy(energy);
//                countEnergyYDimension(x, y, borderCells, boundarySize);
            }
        }
    }

    private void countEnergyIfGrainBorder(int max) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                countEnergyIfGrainsBorder(max, i, j, array, 1);
            }
        }
    }

    private void countEnergyHomogenous(int firstEnergyValue) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j].setEnergy(firstEnergyValue);
            }
        }
    }

    public void switchView() {
        if (energyView) {
            setEnergyView(false);
        } else {
            setEnergyView(true);
        }
    }

    public Cell[][] getArrayToPrint() {
        if (!energyView) {
            return MultiscaleModel.getInstance().getArray();
        } else {
            return MultiscaleModel.getInstance().getEnergyArray();
        }
    }

    public void generateRecrystalizedGrains(Place place, int numberOfNucleons) {
        setNumberOfRecrystalizedNucleons(getNumberOfRecrystalizedNucleons() + numberOfNucleons);
        List<Cell> cells = MultiscaleModelHelper.generateRandomRecrystalizedNucleons(numberOfNucleons);
        int x;
        int y;
        int i = numberOfNucleons - 1;
        Cell c;
        // for(int i = numberOfNucleons - 1; i >= 0; i--){
        while (!cells.isEmpty()) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if (place.equals(Place.BORDERS)) {
                if ((c = array[x][y]).getId() >= 0 && MultiscaleModelHelper.isGrainsBorder(x, y, c.getId(), array) && !c.getState().equals(Cell.State.INCLUSION) && !c.getState().equals(Cell.State.PHASE) && !c.getState().equals(Cell.State.INSIDE_BORDER)) {
                    array[x][y] = cells.remove(i--);
                }
            } else {
                if ((c = array[x][y]).getId() >= 0 && !c.getState().equals(Cell.State.INCLUSION) && !c.getState().equals(Cell.State.PHASE) && !c.getState().equals(Cell.State.INSIDE_BORDER)) {
                    array[x][y] = cells.remove(i--);
                }
            }
        }
        updateEnergyArray();
    }

    private void updateEnergyArray() {
        Cell c;
        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[0].length; j++) {
                if ((c = array[i][j]).getState().equals(Cell.State.RECRYSTALIZED)) {
                    energyArray[i][j].setRgb(new int[]{0, 0, 0});
                }
            }
        }
    }
}