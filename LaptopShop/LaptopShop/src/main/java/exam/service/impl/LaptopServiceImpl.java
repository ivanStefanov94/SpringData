package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.LaptopDto.LaptopSeedDto;
import exam.model.entity.Laptop;
import exam.repository.LaptopRepository;
import exam.service.LaptopService;
import exam.service.ShopService;
import exam.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class LaptopServiceImpl implements LaptopService {

    private static final String LAPTOP_FILE_PATH = "src/main/resources/files/json/laptops.json";
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final LaptopRepository laptopRepository;
    private final ShopService shopService;

    public LaptopServiceImpl(ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, LaptopRepository laptopRepository, ShopService shopService) {
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.laptopRepository = laptopRepository;
        this.shopService = shopService;
    }

    @Override
    public boolean areImported() {
        return laptopRepository.count() > 0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        return Files.readString(Path.of(LAPTOP_FILE_PATH));
    }

    @Override
    public String importLaptops() throws IOException {
        StringBuilder builder = new StringBuilder();

        Arrays.stream(gson.fromJson(readLaptopsFileContent(), LaptopSeedDto[].class))
                .filter(laptopSeedDto -> {
                    boolean isValid = validationUtil.isValid(laptopSeedDto)
                            && !macAddressExists(laptopSeedDto.getMacAddress());

                    builder.append(isValid
                            ? String.format("Successfully imported Laptop %s - %.2f - %d - %d",
                            laptopSeedDto.getMacAddress(),
                            laptopSeedDto.getCpuSpeed(),
                            laptopSeedDto.getRam(),
                            laptopSeedDto.getStorage())
                            : "Invalid Laptop")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(laptopSeedDto -> {
                    Laptop laptop = modelMapper.map(laptopSeedDto, Laptop.class);
                    laptop.setShop(shopService.findShopByName(laptopSeedDto.getShop().getName()));

                    return laptop;
                })
                .forEach(laptopRepository::save);

        return builder.toString();
    }

    private boolean macAddressExists(String macAddress) {
        return laptopRepository.existsByMacAddress(macAddress);
    }

    @Override
    public String exportBestLaptops() {
        StringBuilder builder = new StringBuilder();

        laptopRepository.extractInformationAboutBestLaptops()
                .forEach(laptop -> {
                    builder.append(String.format("Laptop - %s\n" +
                            "*Cpu speed - %.2f\n" +
                            "**Ram - %d\n" +
                            "***Storage - %d\n" +
                            "****Price - %.2f\n" +
                            "#Shop name - %s\n" +
                            "##Town - %s\n",
                            laptop.getMacAddress(),
                            laptop.getCpuSpeed(),
                            laptop.getRam(),
                            laptop.getStorage(),
                            laptop.getPrice(),
                            laptop.getShop().getName(),
                            laptop.getShop().getTown().getName()))
                            .append(System.lineSeparator());
                });

        return builder.toString();
    }
}
