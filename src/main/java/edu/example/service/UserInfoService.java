package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.UserInfo;
import edu.example.repository.UserInfoRepository;
import edu.example.repository.UserRepository;
import edu.example.repository.exception.FileWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;

    private final FileStorageService fileStorageService;


    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository, UserRepository userRepository,
                           FileStorageService fileStorageService) {
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public UserInfo createUserInfo(UserInfo userInfo) {
        if (!userRepository.existsByUsername(userInfo.getUsername()))
            throw new EntityNotFoundException("User not found");

        return userInfoRepository.save(userInfo);
    }

    public UserInfo getUserInfo(String username) {
        var optional = userInfoRepository.findById(username);
        if (optional.isEmpty())
            throw new EntityNotFoundException("User not found");

        return optional.get();
    }

    public UserInfo updateUserInfo(UserInfo userInfo) {
        if (!userInfoRepository.existsById(userInfo.getUsername()))
            throw new EntityNotFoundException("User info not found");

        return userInfoRepository.save(userInfo);
    }

    public String getUserImageName(String username) {
        return userInfoRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User info not found"))
                .getUserImage();
    }

    public String setUserImage(String username, MultipartFile file) {
        if (!userRepository.existsByUsername(username))
            throw new EntityNotFoundException("User not found");

        try {
            var fileName = fileStorageService.saveFile(file);

            var optional = userInfoRepository.findById(username);
            UserInfo userInfo;
            if (optional.isPresent()) {
                userInfo = optional.get();
            } else {
                userInfo = new UserInfo();
                userInfo.setUsername(username);
            }
            userInfo.setUserImage(fileName);
            userInfoRepository.save(userInfo);

            return fileName;
        } catch (FileWriteException e) {
            throw new RuntimeException(e);
        }
    }

}
