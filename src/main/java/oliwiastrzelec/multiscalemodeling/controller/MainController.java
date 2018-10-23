package oliwiastrzelec.multiscalemodeling.controller;

import oliwiastrzelec.multiscalemodeling.model.MultiscaleModel;
import oliwiastrzelec.multiscalemodeling.model.MultiscaleModelHelper;
import oliwiastrzelec.multiscalemodeling.model.Shape;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        MultiscaleModel.getInstance().generateRandomGrains(numberOfNucleons);
        addAttributes(model);
        return "index";
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

    @PostMapping("/generateInclusions")
    public String generateInclusions(@RequestParam("numberOfInclusions") int numberOfInclusions,
                                     @RequestParam("sizeOfInclusions") float sizeOfInclusions,
                                     @RequestParam("shapeOfInclusions") Shape shapeOfInclusions,
                                     Model model) {
        MultiscaleModel.getInstance().generateInclusions(numberOfInclusions, sizeOfInclusions, shapeOfInclusions);
        addAttributes(model);
        return "index";
    }


    private void addAttributes(Model model) {
        model.addAttribute("sizeX", MultiscaleModel.getInstance().getSizeX());
        model.addAttribute("sizeY", MultiscaleModel.getInstance().getSizeY());
        model.addAttribute("array", MultiscaleModel.getInstance().getArray());
        model.addAttribute("grainsGenerated", MultiscaleModel.getInstance().isGrainsGenerated());
        model.addAttribute("arrayFilled", MultiscaleModel.getInstance().isArrayFilled());
//        model.addAttribute("inclusionAdded", MultiscaleModel.getInstance().isInclusionAdded());
    }

}
