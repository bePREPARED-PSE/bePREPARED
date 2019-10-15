package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.restController.exceptions.InternalServerErrorException;
import edu.kit.pse.beprepared.restController.exceptions.NotFoundException;
import edu.kit.pse.beprepared.services.FileManagementService;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * The REST controller for the file management.
 */
@RestController
public class FileManagementController {

    /**
     * The {@link Logger} instance used by this class.
     */
    private final Logger log = Logger.getLogger(FileManagementController.class);

    /**
     * Mapping for {@code /files} (POST).
     *
     * @param file the file
     * @param name the name for the file
     */
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public @ResponseBody
    String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {

        log.info("Received request: \"/files\" Method: POST");
        log.debug("File name is: " + name);

        try {
            return FileManagementService.getInstance().storeFile(file, name, true);
        } catch (IOException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        }

    }

    /**
     * Mapping for {@code /files/{name}} (GET).
     *
     * @param name the name of the file
     * @return an appropriate response entity
     */
    @RequestMapping(value = "/files/{name}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@PathVariable String name) {

        log.info("Received request: \"/files/" + name + "\" Method: GET");

        File file;
        try {
            file = FileManagementService.getInstance().getFile(name);
        } catch (FileNotFoundException e) {
            log.warn(e);
            throw new NotFoundException(e);
        } catch (IOException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        }

        Resource resource;
        try {
            resource = new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            log.warn(e);
            throw new InternalServerErrorException(e);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }

}
