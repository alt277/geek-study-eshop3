package ru.geekbrains.service;



import ru.geekbrains.persist.model.PictureData;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public interface PictureService {

    Optional<String> getPictureContentTypeById(long id);

    Optional<byte[]> getPictureDataById(long id);

    PictureData createPictureData(byte[] picture);

    public void downloadProductPicture( Long pictureId, HttpServletResponse resp) throws IOException;

    public void deleteProductPicture(Long pictureId) ;
}
