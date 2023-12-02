package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.UserInfo;
import edu.example.repository.UserInfoRepository;
import edu.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    UserInfoRepository userInfoRepository;
    UserRepository userRepository;

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository, UserRepository userRepository) {
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
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

}
