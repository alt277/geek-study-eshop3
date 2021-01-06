package ru.geekbrains.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.geekbrains.service.PictureService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/picture")
public class PictureController {

    private static final Logger logger = LoggerFactory.getLogger(PictureController.class);

    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

//    @GetMapping("/{pictureId}")
//    public void downloadProductPicture(@PathVariable("pictureId") Long pictureId, HttpServletResponse resp) throws IOException {
//        logger.info("Downloading picture with id: {}", pictureId);
//
//        Optional<String> opt = pictureService.getPictureContentTypeById(pictureId);
//        if (opt.isPresent()) {
//            resp.setContentType(opt.get());
//            resp.getOutputStream().write(pictureService.getPictureDataById(pictureId).get());
//        } else {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//        }
//    }
        @GetMapping("/{pictureId}")
    public void downloadProductPicture(@PathVariable("pictureId") Long pictureId, HttpServletResponse resp) throws IOException {
        logger.info("Downloading picture with id: {}", pictureId);

      pictureService.downloadProductPicture(pictureId,resp);
    }

    @GetMapping("/{pictureId}/delete")
    public String deleteProductPicture(@PathVariable("pictureId") Long pictureId) throws IOException {
        logger.info("Deleting picture with id: {}", pictureId);
        pictureService.deleteProductPicture(pictureId );
//        @{/picture/{pictureId}/delete (pictureId = ${pict.id})}
        return "redirect:/product/{id}/edit (pictureId = ${pict.id}";
    }
}
