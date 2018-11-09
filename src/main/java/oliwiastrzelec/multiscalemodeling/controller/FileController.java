package oliwiastrzelec.multiscalemodeling.controller;

import oliwiastrzelec.multiscalemodeling.model.MultiscaleModel;
import oliwiastrzelec.multiscalemodeling.model.MultiscaleModelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class FileController {

    @ResponseBody
    @PostMapping("/exportDatafile")
    public String exportDatafile(HttpServletResponse response) {
        String fileName = "datafile.txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        MultiscaleModel instance = MultiscaleModel.getInstance();
        return MultiscaleModelHelper.getDataFile(instance.getArray(), instance.getSizeX(), instance.getSizeY());
    }

    @ResponseBody
    @PostMapping("/exportBitmap")
    public void getFile(HttpServletResponse response) {
        try {
            String fileName = "bitmap.bmp";
            File file = new File("src/main/resources/static/bitmap.bmp");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            MultiscaleModelHelper.getBitmap(MultiscaleModel.getInstance().getArray());
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}