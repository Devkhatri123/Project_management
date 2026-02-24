package com.project_management.project_management.service;
import com.project_management.project_management.Dtos.*;
import com.project_management.project_management.enums.Authority;
import com.project_management.project_management.enums.Role;
import com.project_management.project_management.enums.TokenExpired;
import com.project_management.project_management.exception.user.*;
import com.project_management.project_management.model.*;
import com.project_management.project_management.repository.ForgetPasswordRepo;
import com.project_management.project_management.repository.UserRepository;
import jakarta.mail.MessagingException;
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
    private final EmailService emailService;

    @Autowired
    public AuthService(final UserRepository userRepository, final ModelMapper modelMapper, final BCryptPasswordEncoder bCryptPasswordEncoder, final JwtService jwtService, final RefreshTokenService refreshTokenService,
                       final ForgetPasswordRepo forgetPasswordRepo, final EmailService emailService){
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.forgetPasswordRepo = forgetPasswordRepo;
        this.emailService = emailService;
    }

    @Transactional
    public void register(RegisterRequestDTO registerRequestDTO) throws EmailAlreadyExists, InvalidSelectedRole, MessagingException {
        if(userRepository.existsByEmail(registerRequestDTO.email())){
            throw new EmailAlreadyExists("Email is already taken");
        }
        User user = modelMapper.map(registerRequestDTO, User.class);
        if(registerRequestDTO.usingAs().equals(Role.valueOf(registerRequestDTO.usingAs()).toString())){
            user.setRole(registerRequestDTO.usingAs());
            user.set_enabled(false);
            user.setProfile_pic("https://static.thenounproject.com/png/4154905-200.png");
            user.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
            user.setVerification(createVerification(user));
            user = userRepository.save(user);
            // Send account created successful email
            emailService.registrationSuccessfulEmail(user);
        } else {
          throw new InvalidSelectedRole("Invalid role selected");
        }
    }
    public Map<String, Object> login(LoginRequest loginRequest) throws IncorrectEmail, IncorrectPassword {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IncorrectEmail("Incorrect email. Try again"));
        if(passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
             // Generate a new refresh token and save it in db
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            refreshTokenService.saveRefreshToken(refreshToken);
            return createJwtResponse(user, refreshToken.getToken(), refreshToken.getExpiresOn());
        } else {
           throw new IncorrectPassword("Wrong password");
        }
    }

    private Verification createVerification(User user){
        return Verification.builder()
                 .user(user)
                 .isExpired(false)
                 .otpCode(new Random().nextInt(100000,999999))
                 .expiresAt(LocalDateTime.now().plusMinutes(3))
                 .build();
    }

    @Transactional
    public Map<String, Object> refreshToken(String token,String timeZone) throws TokenExpired {
       RefreshToken refreshToken = refreshTokenService.findRefreshToken(token);
       User user = refreshToken.getUser();

       LocalDateTime userRefreshTokenExpiryInUserTimeZone = convertUTCTimeInUserTimeZone(refreshToken.getExpiresOn(), timeZone);

       if(!userRefreshTokenExpiryInUserTimeZone.isBefore(LocalDateTime.now(ZoneId.of(timeZone)))){
           refreshTokenService.deleteRefreshToken(refreshToken.getToken());
           RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
           refreshTokenService.saveRefreshToken(newRefreshToken);
           return createJwtResponse(user, newRefreshToken.getToken(), newRefreshToken.getExpiresOn());
       }
       log.info("Refresh token expired");
       throw new TokenExpired("Please sign in again");
    }
    private Map<String, Object> createJwtResponse(User user,String refreshToken,LocalDateTime refreshTokenExpiry){
        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwtService.generateJwt(user));
        response.put("refreshToken", refreshToken);
        response.put("expiry",refreshTokenExpiry);
        return response;
    }
    public UserDTO getLoggedInUser(){
     UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     User user = userDetails.getUser();
     UserDTO userDTO = modelMapper.map(user, UserDTO.class);
     userDTO.setAuthorities(assignAuthoritiesToLoggedInUser(user, userDTO));
     return userDTO;
    }
    // Set authorities to loggedInUser
    private List<String> assignAuthoritiesToLoggedInUser(User user, UserDTO userDTO){
        List<String> authorities = new ArrayList<>();
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
        return authorities;
    }
    public void forgetPassword(ForgetPasswordDTO forgetPasswordDTO, String resetToken) throws TokenExpired, MessagingException {
      ForgetPassword forgetPassword = forgetPasswordRepo.findOneByToken(resetToken)
              .orElseThrow(() -> new TokenExpired("Forget password token has been expired"));
      LocalDateTime userTimeZoneExpiry = convertUTCTimeInUserTimeZone(forgetPassword.getExpiry(),forgetPasswordDTO.timeZone());
       if(!userTimeZoneExpiry.isBefore(LocalDateTime.now(ZoneId.of(forgetPasswordDTO.timeZone())))){
          if(forgetPasswordDTO.newPassword().equals(forgetPasswordDTO.confirmPassword())){
              User user = forgetPassword.getUser();
              user.setPassword(passwordEncoder.encode(forgetPasswordDTO.confirmPassword()));
              user.setForgetPassword(null);
              userRepository.save(user);
              // send password changed successfully email
              emailService.PasswordChangedSuccessfully(user);
          }
      }
       throw new TokenExpired("Reset has been expired ");
    }
    private LocalDateTime convertUTCTimeInUserTimeZone(LocalDateTime utcTime, String timeZone){
        return utcTime
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of(timeZone)).toLocalDateTime();
    }

    public void generateForgetPasswordToken(String email) throws IncorrectEmail, MessagingException {
       User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IncorrectEmail("Incorrect email. User doesn't exist of this email"));
      ForgetPassword forgetPassword = createForgetPasswordToken(user);
      user.setForgetPassword(forgetPassword);
      userRepository.save(user);
      // Send forget password link to user's email
      emailService.ForgetPasswordLink(user);

    }
    private ForgetPassword createForgetPasswordToken(User user){
        return ForgetPassword.builder()
                .user(user)
                .token(UUID.randomUUID().toString().substring(0,10))
                .expiry(LocalDateTime.now().plusMinutes(3))
                .is_Active(true)
                .build();
    }

    public void verify(VerifyDTO verifyDTO) throws IncorrectEmail {
      User user = userRepository.findByEmail(verifyDTO.email())
               .orElseThrow(() -> new IncorrectEmail("Invalid email. Try correct email"));
      LocalDateTime userTimeZoneExpiry = convertUTCTimeInUserTimeZone(user.getVerification().getExpiresAt(), verifyDTO.timeZone());
     if(!userTimeZoneExpiry.isBefore(LocalDateTime.now(ZoneId.of(verifyDTO.timeZone())))){
         if(user.getVerification().getOtpCode() == verifyDTO.code()){
             user.set_enabled(true);
             user.setVerification(null);
             userRepository.save(user);
         }
     }
    }
}
