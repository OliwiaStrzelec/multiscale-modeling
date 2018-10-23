package oliwiastrzelec.multiscalemodeling.controller;

import oliwiastrzelec.multiscalemodeling.model.MultiscaleModel;
import oliwiastrzelec.multiscalemodeling.model.MultiscaleModelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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
}