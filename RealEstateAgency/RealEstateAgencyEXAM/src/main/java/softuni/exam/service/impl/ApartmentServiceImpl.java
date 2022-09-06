package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ApartmentSeedRootDto;
import softuni.exam.models.entity.Apartment;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.service.ApartmentService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private static final String APARTMENTS_FILE_PATH = "src/main/resources/files/xml/apartments.xml";

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final ApartmentRepository apartmentRepository;
    private final TownService townService;

    public ApartmentServiceImpl(ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser, ApartmentRepository apartmentRepository, TownService townService) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.apartmentRepository = apartmentRepository;
        this.townService = townService;
    }


    @Override
    public boolean areImported() {
        return apartmentRepository.count() > 0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return Files.readString(Path.of(APARTMENTS_FILE_PATH));
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();

        xmlParser.fromFile(APARTMENTS_FILE_PATH, ApartmentSeedRootDto.class)
                .getApartments()
                .stream()
                .filter(apartmentSeedDto -> {
                    boolean isValid = validationUtil.isValid(apartmentSeedDto)
                            && !apartmentAreaAndTownExists(apartmentSeedDto.getArea(), apartmentSeedDto.getTown());

                    builder.append(isValid
                            ? String.format("Successfully imported apartment %s - %.2f",
                            apartmentSeedDto.getApartmentType(), apartmentSeedDto.getArea())
                            : "Invalid apartment")
                            .append(System.lineSeparator());


                    return isValid;
                })
                .map(apartmentSeedDto -> {
                    Apartment apartment = modelMapper.map(apartmentSeedDto, Apartment.class);
                    apartment.setTown(townService.findTownByName(apartmentSeedDto.getTown()));

                    return apartment;
                })
                .forEach(apartmentRepository::save);

        return builder.toString();
    }

    @Override
    public Apartment findApartmentById(Integer id) {
        return apartmentRepository.findById(id).orElse(null);
    }

    private boolean apartmentAreaAndTownExists(double area, String town) {
        return apartmentRepository.existsByArea(area)
                && apartmentRepository.existsByTown(townService.findTownByName(town));

    }
}
