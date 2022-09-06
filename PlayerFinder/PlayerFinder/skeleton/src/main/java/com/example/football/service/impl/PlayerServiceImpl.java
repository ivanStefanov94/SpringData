package com.example.football.service.impl;

import com.example.football.models.dto.PlayerSeedRootDto;
import com.example.football.models.entity.Player;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final String PLAYER_FILE_PATH = "src/main/resources/files/xml/players.xml";
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final PlayerRepository playerRepository;
    private final ValidationUtil validationUtil;
    private final StatRepository statRepository;
    private final TownRepository townRepository;
    private final TownService townService;
    private final TeamService teamService;

    public PlayerServiceImpl(ModelMapper modelMapper, XmlParser xmlParser, PlayerRepository playerRepository, ValidationUtil validationUtil, StatRepository statRepository, TownRepository townRepository, TownService townService, TeamService teamService) {
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.playerRepository = playerRepository;
        this.validationUtil = validationUtil;
        this.statRepository = statRepository;
        this.townRepository = townRepository;
        this.townService = townService;
        this.teamService = teamService;
    }


    @Override
    public boolean areImported() {
        return playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYER_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder builder = new StringBuilder();

        xmlParser.fromFile(PLAYER_FILE_PATH, PlayerSeedRootDto.class)
                .getPlayers()
                .stream()
                .filter(playerSeedDto -> {
                    boolean isValid = validationUtil.isValid(playerSeedDto);

                    builder.append(isValid
                            ? String.format("Successfully imported Player %s %s - %s",
                            playerSeedDto.getFirstName(), playerSeedDto.getLastName(),playerSeedDto.getPosition())
                            : "Invalid Player")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(playerSeedDto -> {
                    Player player = modelMapper.map(playerSeedDto, Player.class);


                    player.setTown(townService.findByTownName(playerSeedDto.getTown().getName()));
                    player.setStat(statRepository.findById(playerSeedDto.getStat().getId()).orElse(null));
                    player.setTeam(teamService.findByTeamName(playerSeedDto.getTeam().getName()));


                    return player;
                })
                .forEach(playerRepository::save);


        return builder.toString();
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder builder = new StringBuilder();

        LocalDate after = LocalDate.of(1995, 1, 1);
        LocalDate before = LocalDate.of(2003, 1, 1);

         playerRepository.exportInfoAboutBestPlayersWithGivenBirthDateAndOrderedByStats(after,before)
                .forEach(player -> {
                    builder.append(String.format("Player - %s %s\n" +
                            "\tPosition - %s\n" +
                            "\tTeam - %s\n" +
                            "\tStadium - %s\n",
                            player.getFirstName(),
                            player.getLastName(),
                            player.getPosition(),
                            player.getTeam().getName(),
                            player.getTeam().getStadiumName()))
                            .append(System.lineSeparator());
                });

         return builder.toString();
    }
}
