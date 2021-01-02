package ru.geekbrains.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.geekbrains.persist.model.Picture;
import ru.geekbrains.persist.model.PictureData;
import ru.geekbrains.persist.repo.PictureRepository;

import java.util.Optional;

public class PictureServiceFileImpl implements PictureService{

    @Value("S{picture.storage.path")
    private String storagePath;

    private final PictureRepository repository;

    @Autowired
    public PictureServiceFileImpl(PictureRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> getPictureContentTypeById(long id) {
        return repository.findById(id)
                .map(Picture::getContentType);
    }

    @Override
    public Optional<byte[]> getPictureDataById(long id) {
        return
    }

    @Override
    public PictureData createPictureData(byte[] picture) {
        return
    }
}
