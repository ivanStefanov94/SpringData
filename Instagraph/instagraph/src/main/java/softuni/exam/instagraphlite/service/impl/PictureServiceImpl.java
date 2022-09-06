package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.PictureSeedDto;
import softuni.exam.instagraphlite.models.entity.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class PictureServiceImpl implements PictureService{

    private static final String PICTURES_FILE_PATH = "src/main/resources/files/pictures.json";

    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final PictureRepository pictureRepository;

    public PictureServiceImpl(ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, PictureRepository pictureRepository) {
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.pictureRepository = pictureRepository;
    }


    @Override
    public boolean areImported() {
        return pictureRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        StringBuilder builder = new StringBuilder();

        Arrays.stream(gson.fromJson(readFromFileContent(), PictureSeedDto[].class))
                .filter(pictureSeedDto -> {
                    boolean isValid = validationUtil.isValid(pictureSeedDto)
                            && !doesEntityExist(pictureSeedDto.getPath());

                    builder.append(isValid
                            ? String.format("Successfully imported Picture, with size %.2f",pictureSeedDto.getSize())
                            : "Invalid Picture")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(pictureSeedDto -> modelMapper.map(pictureSeedDto, Picture.class))
                .forEach(pictureRepository::save);

        return builder.toString();
    }

    @Override
    public boolean doesEntityExist(String path) {
        return pictureRepository.existsByPath(path);

    }

    @Override
    public Picture findByPath(String profilePicture) {

        return pictureRepository.findByPath(profilePicture).orElse(null);
    }

    @Override
    public String exportPictures() {
        StringBuilder builder = new StringBuilder();

        pictureRepository.exportPicturesWithSizeBiggerThan30000()
                .forEach(picture -> {
                    builder.append(String.format("%.2f - %s", picture.getSize(), picture.getPath()))
                            .append(System.lineSeparator());

                });

        return builder.toString();
    }
}
