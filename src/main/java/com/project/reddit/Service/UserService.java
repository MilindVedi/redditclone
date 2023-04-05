package com.project.reddit.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.reddit.dto.RegisterDto;
import com.project.reddit.entities.NotificationEmail;
import com.project.reddit.entities.Users;
import com.project.reddit.entities.VerficationToken;
import com.project.reddit.repository.UserRepository;
import com.project.reddit.repository.VerificationTokenRepository;
import com.project.reddit.serviceImplementaion.EmailSender;
import com.project.reddit.serviceImplementaion.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private MailService mailService;
    @Autowired
    private VerificationTokenRepository verificationRepository;

    @Autowired
    EmailSender emailSender;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    public void signup(RegisterDto registerDto) {

        Users user = new Users();
        user.setUsername(registerDto.getUserName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please activate your account", user.getEmail(),
                "please click below link : http://localhost:5000/user/accountVerification/" + token));

    }


    public Users findUserByName(String userName) {

        return userRepository.findByUsername(userName);

    }

    public Users getUserByEmail(String email) throws ChangeSetPersister.NotFoundException {
        Users user = userRepository.findByEmail(email);
        return user;
    }

    private String generateVerificationToken(Users user) {
        String token = UUID.randomUUID().toString();
        VerficationToken verificationToken = new VerficationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationRepository.save(verificationToken);
        return token;
    }


    public void verifyAccount(String token) {
        Optional<VerficationToken> verificationToken = verificationRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new RuntimeException("Invalid Token")));
    }

    private void fetchUserAndEnable(VerficationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        Users user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public Users findUserByUserId(long userId) {
        return findUserByUserId(userId);
    }
}

