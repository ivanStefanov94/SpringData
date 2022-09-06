package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.OfferDto.OfferSeedRootDto;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.AgentService;
import softuni.exam.service.ApartmentService;
import softuni.exam.service.OfferService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OfferServiceImpl implements OfferService {

    private static final String OFFERS_FILE_PATH = "src/main/resources/files/xml/offers.xml";

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final OfferRepository offerRepository;
    private final AgentService agentService;
    private final ApartmentService apartmentService;

    public OfferServiceImpl(ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser, OfferRepository offerRepository, AgentService agentService, ApartmentService apartmentService) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.offerRepository = offerRepository;
        this.agentService = agentService;
        this.apartmentService = apartmentService;
    }

    @Override
    public boolean areImported() {
        return offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(OFFERS_FILE_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder builder = new StringBuilder();

        xmlParser.fromFile(OFFERS_FILE_PATH, OfferSeedRootDto.class)
                .getOffers()
                .stream()
                .filter(offerSeedDto -> {
                    boolean isValid = validationUtil.isValid(offerSeedDto)
                            && agentService.agentFirstNameExists(offerSeedDto.getAgent().getFirstName());

                builder.append(isValid
                        ? String.format("Successfully imported offer %.2f", offerSeedDto.getPrice())
                        : "Invalid offer")
                        .append(System.lineSeparator());

                return isValid;
                })
                .map(offerSeedDto -> {
                    Offer offer = modelMapper.map(offerSeedDto, Offer.class);
                    offer.setAgent(agentService.findAgentByName(offerSeedDto.getAgent().getFirstName()));
                    offer.setApartment(apartmentService.findApartmentById(offerSeedDto.getApartment().getId()));

                    return offer;
                })
                .forEach(offerRepository::save);

        return builder.toString();
    }


    @Override
    public String exportOffers() {
        StringBuilder builder = new StringBuilder();

        offerRepository.exportBestOffers()
                .forEach(offer -> {
                    builder.append(String.format("Agent %s %s with offer №%d:\n" +
                            "   \t\t-Apartment area: %.2f\n" +
                            "   \t\t--Town: %s\n" +
                            "   \t\t---Price: %.2f$\n",
                            offer.getAgent().getFirstName(),
                            offer.getAgent().getLastName(),
                            offer.getId(),
                            offer.getApartment().getArea(),
                            offer.getApartment().getTown().getTownName(),
                            offer.getPrice()))
                            .append(System.lineSeparator());

                });

        return builder.toString();
    }
}
