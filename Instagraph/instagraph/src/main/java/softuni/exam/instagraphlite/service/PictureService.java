package softuni.exam.instagraphlite.service;

import org.springframework.stereotype.Repository;
import softuni.exam.instagraphlite.models.entity.Picture;

import java.io.IOException;


public interface PictureService {
    boolean areImported();
    String readFromFileContent() throws IOException;
    String importPictures() throws IOException;

    boolean doesEntityExist(String path);

    String exportPictures();

    Picture findByPath(String profilePicture);
}
