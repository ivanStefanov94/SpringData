package com.example.football.service.impl;

import com.example.football.models.dto.TownSeedDto;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


@Service
public class TownServiceImpl implements TownService {

    private static final String TOWNS_FILE_PATH = "src/main/resources/files/json/towns.json";
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final TownRepository townRepository;

    public TownServiceImpl(ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, TownRepository townRepository) {
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
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

                    builder.append(isValid ?
                            String.format("Successfully imported Town %s - %d", townSeedDto.getName(),
                                    townSeedDto.getPopulation())
                            : "Invalid Town")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(townSeedDto -> modelMapper.map(townSeedDto, Town.class))
                .forEach(townRepository::save);

        return builder.toString();
    }

    @Override
    public Town findByTownName(String town) {
        return townRepository.findByName(town).orElse(null);
    }


}
