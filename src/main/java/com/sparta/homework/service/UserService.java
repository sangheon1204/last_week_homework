package com.sparta.homework.service;

import com.sparta.homework.dto.UserRequestDto;
import com.sparta.homework.dto.ResponseDto;
import com.sparta.homework.dto.LoginResponseDto;
import com.sparta.homework.entity.User;
import com.sparta.homework.entity.UserRoleEnum;
import com.sparta.homework.jwt.JwtUtil;
import com.sparta.homework.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final String ADMIN_TOKEN = "1";



    @Transactional
    public ResponseDto signup(UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();
        Optional<User> found = userRepository.findByUsername(username);
        //아이디 중복 확인
        if(found.isPresent()) {
            throw new IllegalArgumentException("중복된 username입니다.");
        }
        //사용자 Role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if(userRequestDto.isAdmin()) {
            if(!userRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }
        User user = new User(username, password, role);
        userRepository.save(user);
        ResponseDto responseDto = new ResponseDto("회원 가입 성공", HttpStatus.OK.value());
        return responseDto;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(UserRequestDto userRequestDto, HttpServletResponse response) {
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();

        //사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );
        if(!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(),user.getRole()));
        LoginResponseDto loginResponseDto = new LoginResponseDto("로그인 성공",200);
        return loginResponseDto;
    }
}
