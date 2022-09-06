package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TownSeedDto;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class TownServiceImpl implements TownService {

    private static final String TOWNS_FILE_PATH = "src/main/resources/files/json/towns.json";

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final TownRepository townRepository;

    public TownServiceImpl(ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, TownRepository townRepository) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.townRepository = townRepository;
    }

    @Override
    public boolean areImported() {
        return townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWNS_FILE_PATH));
    }

    @Override
    public String importTowns() throws IOException {
        StringBuilder builder = new StringBuilder();

        Arrays.stream(gson.fromJson(readTownsFileContent(), TownSeedDto[].class))
                .filter(townSeedDto -> {
                    boolean isValid = validationUtil.isValid(townSeedDto);

                    builder.append(isValid
                            ? String.format("Successfully imported town %s - %d",
                            townSeedDto.getTownName(), townSeedDto.getPopulation())
                            : "Invalid town")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(townSeedDto -> modelMapper.map(townSeedDto, Town.class))
                .forEach(townRepository::save);

        return builder.toString();
    }

    @Override
    public Town findTownByName(String town) {

        return townRepository.findByTownName(town).orElse(null);
    }


}
