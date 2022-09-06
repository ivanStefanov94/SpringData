package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.CustomerDto.CustomerSeedDto;
import exam.model.entity.Customer;
import exam.repository.CustomerRepository;
import exam.service.CustomerService;
import exam.service.TownService;
import exam.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final String CUSTOMERS_FILE_PATH = "src/main/resources/files/json/customers.json";
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final CustomerRepository customerRepository;
    private final TownService townService;

    public CustomerServiceImpl(ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, CustomerRepository customerRepository, TownService townService) {
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.customerRepository = customerRepository;
        this.townService = townService;
    }

    @Override
    public boolean areImported() {
        return customerRepository.count() > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        return Files.readString(Path.of(CUSTOMERS_FILE_PATH));
    }

    @Override
    public String importCustomers() throws IOException {
        StringBuilder builder = new StringBuilder();

        Arrays.stream(gson.fromJson(readCustomersFileContent(), CustomerSeedDto[].class))
                .filter(customerSeedDto -> {
                    boolean isValid = validationUtil.isValid(customerSeedDto)
                            && !customerEmailExists(customerSeedDto.getEmail());

                    builder.append(isValid
                            ? String.format("Successfully imported Customer %s %s - %s",
                            customerSeedDto.getFirstName(),customerSeedDto.getLastName(),customerSeedDto.getEmail())
                            : "Invalid customer")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(customerSeedDto -> {
                    Customer customer = modelMapper.map(customerSeedDto, Customer.class);
                    customer.setTown(townService.getTownByName(customerSeedDto.getTown().getName()));

                    return customer;
                })
                .forEach(customerRepository::save);

        return builder.toString();
    }

    private boolean customerEmailExists(String email) {
        return customerRepository.existsByEmail(email);
    }
}
