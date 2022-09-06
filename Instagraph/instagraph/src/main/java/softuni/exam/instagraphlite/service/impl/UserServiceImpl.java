package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dto.UserSeedDto;
import softuni.exam.instagraphlite.models.entity.User;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    private static final String USERS_FILE_PATH = "src/main/resources/files/users.json";

    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final UserRepository userRepository;
    private final PictureService pictureService;

    public UserServiceImpl(ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, UserRepository userRepository, PictureService pictureService) {
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.userRepository = userRepository;
        this.pictureService = pictureService;
    }

    @Override
    public boolean areImported() {
        return userRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USERS_FILE_PATH));
    }

    @Override
    public String importUsers() throws IOException {
        StringBuilder builder = new StringBuilder();

        Arrays.stream(gson
                .fromJson(readFromFileContent(), UserSeedDto[].class))
                .filter(userSeedDto -> {
                    boolean isValid = validationUtil.isValid(userSeedDto)
                            && !doesUserExist(userSeedDto.getUsername())
                            && pictureService.doesEntityExist(userSeedDto.getProfilePicture());

                    builder
                            .append(isValid
                                    ? String.format("Successfully imported User: %s", userSeedDto.getUsername())
                                    : "Invalid user")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(userSeedDto -> {
                    User user = modelMapper.map(userSeedDto, User.class);
                    user.setProfilePicture(pictureService.findByPath(userSeedDto.getProfilePicture()));

                    return user;
                })
                .forEach(userRepository::save);

        return builder.toString();
    }

    @Override
    public boolean doesUserExist(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findByUsernameMethod(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public String exportUsersWithTheirPosts() {
        StringBuilder builder = new StringBuilder();

        userRepository.findUsersAndTheirPostsInformation()
                .forEach(user -> {
                    builder.append(String.format("User: %s\n" +
                            "Post count: %d\n",
                            user.getUsername(),
                            user.getPosts().size()));

                    user.getPosts()
                            .forEach(post -> {
                            builder.append(String.format("==Post Details:\n" +
                                    "----Caption: %s\n" +
                                    "----Picture Size: %.2f\n",
                                    post.getCaption(),
                                    post.getPicture().getSize()));
                            });
                });


        return builder.toString();
    }
}
