package br.com.guilhermespadaro.service;

import br.com.guilhermespadaro.domain.User;
import br.com.guilhermespadaro.exception.ResourceNotFoundException;
import br.com.guilhermespadaro.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    public User update(Long userId, User request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return userRepository.save(user);
    }

    public User updatePassword(Long id, String previousPassword, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        if (!passwordEncoder.matches(previousPassword, user.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public void delete(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        userRepository.delete(user);
    }
}