package com.example.SUB.Service;

import com.example.SUB.Entity.SiteUser;
import com.example.SUB.Repository.UserRepository;
import com.example.SUB.error.DataNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        if(userRepository.existsByUsername(username)){
            throw new DataIntegrityViolationException("username 이미 사용 중입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("email 이미 존재하는 이메일입니다.");
        }

        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    // getUser 메서드는 userRepository의 findByUsername 메서드를 사용하여 쉽게 만들 수 있다.
    // 사용자명에 해당하는 데이터가 없을 경우에는 DataNoFoundException이 발생하도록 했다.
    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}
