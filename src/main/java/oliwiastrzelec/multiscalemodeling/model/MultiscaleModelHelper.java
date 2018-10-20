package oliwiastrzelec.multiscalemodeling.model;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

//    public static File getBitmap(Cell[][] array, int sizeX, int sizeY) throws IOException {
//        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_4BYTE_ABGR);
//        Cell cell;
//        for (int i = 0; i < sizeX; i++) {
//            for (int j = 0; j < sizeY; j++) {
//                cell = array[i][j];
////                image.setRGB(i, j, ((cell.getRgb()[0]&0x0ff)<<16)|((cell.getRgb()[1]&0x0ff)<<8)|(cell.getRgb()[2]&0x0ff));
//                image.setRGB(i, j, (cell.getRgb()[0] << 16 | cell.getRgb()[1] << 8 | cell.getRgb()[2]));
//            }
//        }
//        File file = new File("bitmap.png");
//        ImageIO.write(image, "png", file);
//        return file;
//    }

    public static void printArray(Cell[][] array) {
        List<Cell[]> list = new ArrayList(Arrays.asList(array));
        list.forEach(cells -> System.out.println(Arrays.asList(cells)));
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

    public static int[] generateRandomColor() {
        int[] color = new int[3];
        color[0] = (int) (Math.floor(Math.random() * 220) + 36);
        color[1] = 0;//(int) (Math.floor(Math.random() * 255) + 1);
        color[2] = (int) (Math.floor(Math.random() * 220) + 36);
        return color;
    }

    public static Cell mostCommon(List<Cell> grainsCount) {
        BidiMap<Cell, Integer> bidiMap = new TreeBidiMap<>(toFrequencyMap(grainsCount));
        return bidiMap.getKey(Collections.max(bidiMap.values()));
    }
}
