package app.service.user;

import app.mapper.user.UserMapper;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto login(UserLoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()
                || !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            throw new RuntimeException("Username or password mismatch!");
        }

        return UserMapper.toUserDto(optionalUser.get());
    }

    public UserDto register(UserRegisterRequest request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(user -> {
                    throw new RuntimeException("User with this username already exists!");
                });

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException("User with this email already exists!");
                });

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userRepository.save(UserMapper.toUserEntity(request));

        return UserMapper.toUserDto(user);
    }

    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(id)));

        return UserMapper.toUserDto(user);
    }
}
