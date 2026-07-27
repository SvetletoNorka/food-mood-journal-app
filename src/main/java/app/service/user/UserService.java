package app.service.user;

import app.exception.DuplicateEmailException;
import app.exception.DuplicateUsernameException;
import app.exception.PasswordMismatchException;
import app.exception.UserNotFoundException;
import app.mapper.user.UserMapper;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    public UserDto register(UserRegisterRequest request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(user -> {
                    throw new DuplicateUsernameException();
                });

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateEmailException();
                });

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userRepository.save(UserMapper.toUserEntity(request));

        return UserMapper.toUserDto(user);
    }

    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public UserDto updateProfile(UUID userId, EditProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existing -> {
                        throw new DuplicateEmailException();
                    });
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getNewPassword())) {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new PasswordMismatchException();
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        user.setUpdatedOn(LocalDateTime.now());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public void switchStatus(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(!user.isActive());
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    public void switchRole(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }
}
