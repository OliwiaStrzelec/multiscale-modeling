package oliwiastrzelec.multiscalemodeling.controller;

import oliwiastrzelec.multiscalemodeling.model.MultiscaleModel;
import oliwiastrzelec.multiscalemodeling.model.Shape;
import oliwiastrzelec.multiscalemodeling.model.Structure;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/addPropability")
    public String addPropability(@RequestParam("probability") int probability, Model model) {
        MultiscaleModel.getInstance().setProbability(probability);
        MultiscaleModel.getInstance().setProbabilityAdded(true);
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


    @PostMapping("/chooseStructure")
    public String chooseStructure(@RequestParam("structure") Structure structure,
                                  @RequestParam("numberOfGrainsToStay") int numberOfGrainsToStay,
                                  Model model) {
        MultiscaleModel.getInstance().chooseStructure(structure, numberOfGrainsToStay);
        addAttributes(model);
        return "index";
    }


    private void addAttributes(Model model) {
        model.addAttribute("sizeX", MultiscaleModel.getInstance().getSizeX());
        model.addAttribute("sizeY", MultiscaleModel.getInstance().getSizeY());
        model.addAttribute("array", MultiscaleModel.getInstance().getArray());
        model.addAttribute("grainsGenerated", MultiscaleModel.getInstance().isGrainsGenerated());
        model.addAttribute("arrayFilled", MultiscaleModel.getInstance().isArrayFilled());
        model.addAttribute("probabilityAdded", MultiscaleModel.getInstance().isProbabilityAdded());
        model.addAttribute("probability", MultiscaleModel.getInstance().getProbability());
        model.addAttribute("structureChoosen", MultiscaleModel.getInstance().isStructureChoosen());
        if (MultiscaleModel.getInstance().isStructureChoosen() || MultiscaleModel.getInstance().isGrainsGenerated()) {
            model.addAttribute("structure", MultiscaleModel.getInstance().getStructure().getStructure());
        }

        model.addAttribute("numberOfGrains", MultiscaleModel.getInstance().getNumberOfGrains());
//        model.addAttribute("inclusionAdded", MultiscaleModel.getInstance().isInclusionAdded());
    }

}
