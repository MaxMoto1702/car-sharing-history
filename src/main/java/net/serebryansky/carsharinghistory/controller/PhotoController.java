package net.serebryansky.carsharinghistory.controller;

import net.serebryansky.carsharinghistory.domain.Photo;
import net.serebryansky.carsharinghistory.domain.User;
import net.serebryansky.carsharinghistory.repository.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("photos")
public class PhotoController {
    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

    private final PhotoRepository photoRepository;

    public PhotoController(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @RequestMapping(value = "/{id}.jpg", method = RequestMethod.GET)
    public void registration(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Photo photo = photoRepository.getOne(id);
        response.setStatus(HttpStatus.OK.value());
        response.getOutputStream().write(photo.getBytes());
    }
}