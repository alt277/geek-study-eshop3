package ru.geekbrains.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.geekbrains.persist.model.Picture;
import ru.geekbrains.persist.model.PictureData;
import ru.geekbrains.persist.repo.PictureRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service                            // условие в Application.properties для созданмя бина
@ConditionalOnProperty(name = "picture.storage.type", havingValue = "file")
public class PictureServiceFileImpl implements PictureService {

    private static final Logger logger = LoggerFactory.getLogger(PictureServiceFileImpl.class);

    @Value("${picture.storage.path}")
    private String storagePath;

    private final PictureRepository repository;

    @Autowired
    public PictureServiceFileImpl(PictureRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> getPictureContentTypeById(long id) {
        return repository.findById(id)
                .filter(picture -> picture.getPictureData().getFileName() != null)
                .map(Picture::getContentType);
    }

    @Override
    public Optional<byte[]> getPictureDataById(long id) {
        return repository.findById(id)
                .filter(picture -> picture.getPictureData().getFileName() != null)
                .map(picture -> Path.of(storagePath, picture.getPictureData().getFileName()))
                .filter(Files::exists)
                .map(path -> {
                    try {
                        return Files.readAllBytes(path);
                    } catch (IOException ex) {
                        logger.error("Can't open picture file", ex);
                        throw new RuntimeException(ex);
                    }
                });
    }

    @Override
    public PictureData createPictureData(byte[] picture) {

        String fileName = UUID.randomUUID().toString();
        System.out.println("fileName=" + fileName);
        System.out.println("storagePath=" + storagePath);
        try (OutputStream os = Files.newOutputStream(Path.of(storagePath, fileName))) {
            os.write(picture);
        } catch (IOException ex) {
            logger.error("Can't create picture file", ex);
            throw new RuntimeException(ex);
        }
        return new PictureData(fileName);
    }

    public void downloadProductPicture(Long pictureId, HttpServletResponse resp) throws IOException {
        logger.info("Downloading picture with id: {}", pictureId);

        Optional<String> opt = getPictureContentTypeById(pictureId);
        if (opt.isPresent()) {
            resp.setContentType(opt.get());
            resp.getOutputStream().write(getPictureDataById(pictureId).get());
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void deleteProductPicture(Long pictureId) throws IOException {

        String fileName = repository.findById(pictureId).get().getPictureData().getFileName();
        Files.deleteIfExists(Paths.get(storagePath + "/" + fileName));
        System.out.println("Внутри  метода DELETE ");
        System.out.println("fileName= " + fileName);
    }

}
