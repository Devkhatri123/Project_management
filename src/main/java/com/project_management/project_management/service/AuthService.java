package com.project_management.project_management.service;
import com.project_management.project_management.Dtos.*;
import com.project_management.project_management.enums.Authority;
import com.project_management.project_management.enums.Role;
import com.project_management.project_management.enums.TokenExpired;
import com.project_management.project_management.exception.user.*;
import com.project_management.project_management.model.*;
import com.project_management.project_management.repository.ForgetPasswordRepo;
import com.project_management.project_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ForgetPasswordRepo forgetPasswordRepo;

    @Autowired
    public AuthService(final UserRepository userRepository, final ModelMapper modelMapper, final BCryptPasswordEncoder bCryptPasswordEncoder, final JwtService jwtService, final RefreshTokenService refreshTokenService,
                       final ForgetPasswordRepo forgetPasswordRepo){
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.forgetPasswordRepo = forgetPasswordRepo;
    }

    @Transactional
    public void register(RegisterRequestDTO registerRequestDTO) throws EmailAlreadyExists, InvalidSelectedRole {
        if(userRepository.existsByEmail(registerRequestDTO.email())){
            throw new EmailAlreadyExists("Email is already taken");
        }
        User user = modelMapper.map(registerRequestDTO, User.class);
        if(registerRequestDTO.usingAs().equals(Role.valueOf(registerRequestDTO.usingAs()).toString())){
            user.setRole(registerRequestDTO.usingAs());
            user.set_enabled(false);
            user.setProfile_pic("https://static.thenounproject.com/png/4154905-200.png");
            user.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
            user.setVerification(createVerification(user,"EMAIL"));
            userRepository.save(user);
        } else {
          throw new InvalidSelectedRole("Invalid role selected");
        }
    }
    public Map<String, Object> login(LoginRequest loginRequest) throws IncorrectEmail, IncorrectPassword {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IncorrectEmail("Incorrect email. Try again"));
        if(passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            String jwt = jwtService.generateJwt(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            refreshTokenService.saveRefreshToken(refreshToken);
            Map<String, Object> response = new HashMap<>();
            response.put("jwt", jwt);
            response.put("refreshToken", refreshToken.getToken());
            return response;
        } else {
           throw new IncorrectPassword("Wrong password");
        }
    }

    private Verification createVerification(User user, String verificationType){
        return Verification.builder()
                 .user(user)
                 .isExpired(false)
                 .otpCode(new Random().nextInt(100000,999999))
                 .verificationType(verificationType)
                 .expiresAt(LocalDateTime.now().plusMinutes(3))
                 .build();
    }

    @Transactional
    public Map<String, Object> refreshToken(String token,String timeZone) throws TokenExpired {
       RefreshToken refreshToken = refreshTokenService.findRefreshToken(token);
       User user = refreshToken.getUser();

       LocalDateTime userRefreshTokenExpiryInUserTimeZone = refreshToken
               .getExpiresOn()
               .atZone(ZoneId.of("UTC"))
               .withZoneSameInstant(ZoneId.of(timeZone)).toLocalDateTime();

       if(!userRefreshTokenExpiryInUserTimeZone.isBefore(LocalDateTime.now(ZoneId.of(timeZone)))){
           refreshTokenService.deleteRefreshToken(refreshToken.getToken());
           String jwt = jwtService.generateJwt(user);
           RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
           refreshTokenService.saveRefreshToken(refreshToken);
           Map<String, Object> response = new HashMap<>();
           response.put("jwt", jwt);
           response.put("refreshToken", newRefreshToken.getToken());
           response.put("expiry",refreshToken.getExpiresOn());
           return response;
       }
       log.info("Refresh token expired");
       throw new TokenExpired("Please sign in again");
    }
    public UserDTO getLoggedInUser(){
     UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     User user = userDetails.getUser();
     UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        List<String> authorities = new ArrayList<>();
        // Normal User authorities (Worker)
        if(!userDTO.getRole().equals(Role.NONE.toString())) {
            authorities.add(Authority.CAN_VIEW_ASSIGNED_TASK.name());
            authorities.add(Authority.CAN_COMPLETE_TASK.name());

            if (user.getRole().equals(Role.OWNER.toString())) {
                authorities.add(Authority.CAN_CREATE_WORKSPACE.name());
                authorities.add(Authority.CAN_INVITE_NEW_USER.name());
                authorities.add(Authority.CAN_CREATE_TASK.name());
                authorities.add(Authority.CAN_CREATE_PROJECT.name());
                authorities.add(Authority.CAN_ASSIGN_TASK_TO_MEMBERS.name());
                authorities.add(Authority.CAN_REMOVE_MEMBER.name());
                authorities.add(Authority.CAN_DELETE_PROJECT.name());
                authorities.add(Authority.CAN_DELETE_WORKSPACE.name());
                userDTO.setAuthorities(authorities);
            }
        }
        return userDTO;
    }
    public void forgetPassword(ForgetPasswordDTO forgetPasswordDTO, String resetToken) throws TokenExpired {
      ForgetPassword forgetPassword = forgetPasswordRepo.findOneByToken(resetToken)
              .orElseThrow(() -> new TokenExpired("Forget password token has been expired"));
      LocalDateTime userTimeZoneExpiry = convertUTCTimeInUserTimeZone(forgetPassword.getExpiry(),forgetPasswordDTO.timeZone());
       if(!userTimeZoneExpiry.isBefore(LocalDateTime.now(ZoneId.of(forgetPasswordDTO.timeZone())))){
          if(forgetPasswordDTO.newPassword().equals(forgetPasswordDTO.confirmPassword())){
              User user = forgetPassword.getUser();
              user.setPassword(passwordEncoder.encode(forgetPasswordDTO.confirmPassword()));
              user.setForgetPassword(null);
              userRepository.save(user);
          }
      }
       throw new TokenExpired("Reset has been expired ");
    }
    private LocalDateTime convertUTCTimeInUserTimeZone(LocalDateTime utcTime, String timeZone){
        return utcTime
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of(timeZone)).toLocalDateTime();
    }

    public void generateForgetPasswordToken(String email) throws IncorrectEmail {
       User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IncorrectEmail("Incorrect email. User doesn't exist of this email"));
      ForgetPassword forgetPassword = createForgetPasswordToken(user);
      user.setForgetPassword(forgetPassword);
      userRepository.save(user);
      // Send forget password link to user's email
    }
    private ForgetPassword createForgetPasswordToken(User user){
        return ForgetPassword.builder()
                .user(user)
                .token(UUID.randomUUID().toString().substring(0,10))
                .expiry(LocalDateTime.now().plusMinutes(3))
                .is_Active(true)
                .build();
    }
}
