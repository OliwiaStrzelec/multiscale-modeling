package oliwiastrzelec.multiscalemodeling.controller;

import oliwiastrzelec.multiscalemodeling.model.MultiscaleModel;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.activation.DataSource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class MainController {

    @GetMapping("/")
    public String getIndex(Model model) {
        addAttributes(model);
        return "index";
    }

    @PostMapping("/generateGrains")
    public String generateGrains(@RequestParam("numberOfNucleons") int numberOfNucleons,
                                 Model model) {
        MultiscaleModel.getInstance().clear();
        MultiscaleModel.getInstance().generateRandomGrains(numberOfNucleons);
        addAttributes(model);
        return "index";
    }

    @ResponseBody
    @PostMapping("/exportDatafile")
    public String exportDatafile(HttpServletResponse response) {
        String fileName = "datafile.txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        return MultiscaleModel.getInstance().getDataFile();
    }

    @PostMapping("/exportBitmap")
    public ResponseEntity<ByteArrayResource> exportBitmap() throws IOException {
        File file = MultiscaleModel.getInstance().getBitmap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + file.getName())
                .contentType(MediaType.IMAGE_PNG).contentLength(data.length)
                .body(resource);

    }

    @PostMapping("/clear")
    public String clear(Model model) {
        MultiscaleModel.getInstance().clear();
        addAttributes(model);
        return "index";
    }

    @PostMapping("/growGrains")
    public String growGrains(@RequestParam("numberOfIterations") int numberOfIterations, Model model) {
        MultiscaleModel.getInstance().growGrainsLoop(numberOfIterations);
        addAttributes(model);
        return "index";
    }


    private void addAttributes(Model model) {
        model.addAttribute("sizeX", MultiscaleModel.getInstance().getSizeX());
        model.addAttribute("sizeY", MultiscaleModel.getInstance().getSizeY());
        model.addAttribute("array", MultiscaleModel.getInstance().getArray());
        model.addAttribute("grainsGenerated", MultiscaleModel.getInstance().isGrainsGenerated());
        model.addAttribute("arrayFilled", MultiscaleModel.getInstance().isArrayFilled());
    }

}
