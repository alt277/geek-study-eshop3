package ru.geekbrains.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.persist.model.Picture;
import ru.geekbrains.persist.model.PictureData;
import ru.geekbrains.persist.repo.PictureRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service                          // условие в Application.properties для созданмя бина
@ConditionalOnProperty(name ="picture.storage.type",havingValue = "database")
public class PictureServiceBlobImpl implements PictureService {

    private final PictureRepository repository;

    @Autowired
    public PictureServiceBlobImpl(PictureRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> getPictureContentTypeById(long id) {
        return repository.findById(id)
                .filter(picture -> picture.getPictureData().getData() !=null)
                .map(Picture::getContentType);
    }

    @Override
    public Optional<byte[]> getPictureDataById(long id) {
        return repository.findById(id)
                .filter(picture -> picture.getPictureData().getData() !=null)
                .map(pic -> pic.getPictureData().getData());
    }

    @Override
    public PictureData createPictureData(byte[] picture) {
        return new PictureData(picture);
    }

    @Override
    public void downloadProductPicture(Long pictureId, HttpServletResponse resp) throws IOException {

        Optional<String> opt = getPictureContentTypeById(pictureId);
        if (opt.isPresent()) {
            resp.setContentType(opt.get());
            resp.getOutputStream().write(getPictureDataById(pictureId).get());
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteProductPicture(Long pictureId)  {
        repository.deleteById(pictureId);

    }
}
