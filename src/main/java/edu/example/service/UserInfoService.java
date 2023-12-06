package edu.example.service;

import edu.example.dto.userInfo.UserInfoCreateUpdateDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.mapper.UserInfoMapper;
import edu.example.model.User;
import edu.example.model.UserInfo;
import edu.example.repository.UserInfoRepository;
import edu.example.repository.UserRepository;
import edu.example.repository.exception.FileWriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;

    private final FileStorageService fileStorageService;
    private final UserInfoMapper userInfoMapper;

    public UserInfo createOrUpdateUserInfo(UserInfoCreateUpdateDto userInfoDto, User user) {
        UserInfo userInfo = userInfoMapper.toUserInfo(userInfoDto);
        userInfo.setUsername(user.getUsername());

        return userInfoRepository.save(userInfo);
    }

    public UserInfo getUserInfo(String username) {
        var optional = userInfoRepository.findById(username);
        if (optional.isEmpty())
            throw new EntityNotFoundException("User not found");

        return optional.get();
    }

    /**
     * Saves user profile image in object storage. Generated file name is saved into UserInfo object for that user.
     * If UserInfo object does not exist it will be created.
     * @param username username of a user
     * @param file file to save
     * @return generated file name
     */
    public String setUserProfileImage(String username, MultipartFile file) {
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
