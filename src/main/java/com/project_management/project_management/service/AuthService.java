package com.project_management.project_management.service;
import com.project_management.project_management.Dtos.*;
import com.project_management.project_management.Dtos.User.ForgetPasswordDTO;
import com.project_management.project_management.Dtos.User.LoginRequest;
import com.project_management.project_management.Dtos.User.RegisterRequestDTO;
import com.project_management.project_management.Dtos.User.UserDTO;
import com.project_management.project_management.enums.User_Enums.Authority;
import com.project_management.project_management.enums.User_Enums.Role;
import com.project_management.project_management.event.UserCreatedEvent;
import com.project_management.project_management.exception.InvalidPlanSelected;
import com.project_management.project_management.exception.Token.TokenExpired;
import com.project_management.project_management.exception.Token.TokenNotFound;
import com.project_management.project_management.exception.user.*;
import com.project_management.project_management.model.*;
import com.project_management.project_management.repository.ForgetPasswordRepo;
import com.project_management.project_management.repository.UserRepository;
import com.project_management.project_management.util.UserUtil;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final SubscriptionService subscriptionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AuthService(final UserRepository userRepository, final ModelMapper modelMapper, final BCryptPasswordEncoder bCryptPasswordEncoder, final JwtService jwtService, final RefreshTokenService refreshTokenService,
                       final ForgetPasswordRepo forgetPasswordRepo, final EmailService emailService, final SubscriptionService subscriptionService,
                       final ApplicationEventPublisher applicationEventPublisher){
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.forgetPasswordRepo = forgetPasswordRepo;
        this.emailService = emailService;
        this.subscriptionService = subscriptionService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

      // @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
      public void register(RegisterRequestDTO registerRequestDTO) throws EmailAlreadyExists, InvalidSelectedRole, MessagingException, InvalidPlanSelected, DataIntegrityViolationException, UserNameAlreadyTaken {
        if(userRepository.existsByEmail(registerRequestDTO.email())){
            throw new EmailAlreadyExists("Email is already taken");
        } else if (userRepository.existsByName(registerRequestDTO.name())){
             throw new UserNameAlreadyTaken("username is already taken. Choose other username");
        }
        User user = modelMapper.map(registerRequestDTO, User.class);
        user.setRole(registerRequestDTO.role().equals("Company") ? Role.OWNER : Role.WORKER);
            user.set_enabled(false);
            user.setTitle("None");
            user.setProfile_pic("https://static.thenounproject.com/png/4154905-200.png");
            user.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
            user.setVerification(createVerification(user));
            user.setSubscription(subscriptionService.createSubscription("BASIC"));
            user = userRepository.save(user);
            // Send account creation successful email
            applicationEventPublisher.publishEvent(new UserCreatedEvent(user));
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
                 .expiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(3))
                 .build();
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public Map<String, Object> refreshJwtToken(String token) throws TokenExpired, TokenNotFound {
       RefreshToken refreshToken = refreshTokenService.findRefreshToken(token);
       if(refreshToken == null){
           throw new TokenNotFound("The refresh token is invalid or expired.");
       }
       User user = refreshToken.getUser();

       if(!refreshToken.getExpiresOn().isBefore(LocalDateTime.now(ZoneOffset.UTC))){
           refreshTokenService.deleteRefreshToken(refreshToken.getToken());
           RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
           refreshTokenService.saveRefreshToken(newRefreshToken);
           return createJwtResponse(user, newRefreshToken.getToken(), newRefreshToken.getExpiresOn());
       }
       log.info("Refresh token expired");
       throw new TokenExpired("The refresh token is expired");
    }
    private Map<String, Object> createJwtResponse(User user,String refreshToken,LocalDateTime refreshTokenExpiry){
        Map<String, Object> response = new HashMap<>();
        response.put("jwt", jwtService.generateJwt(user));
        response.put("refreshToken", refreshToken);
        return response;
    }
    public UserDTO getLoggedInUser(){
     User user = UserUtil.getCurrentUser();
     UserDTO userDTO = modelMapper.map(user, UserDTO.class);
     userDTO.set_enabled(user.is_enabled());
     userDTO.setAuthorities(assignAuthoritiesToLoggedInUser(user, userDTO));
     return userDTO;
    }
    // Set authorities to loggedInUser
    private List<String> assignAuthoritiesToLoggedInUser(User user, UserDTO userDTO){
        List<String> authorities = new ArrayList<>();
        authorities.add(Authority.CAN_VIEW_ASSIGNED_TASK.name());
        authorities.add(Authority.CAN_COMPLETE_TASK.name());

        if (user.getRole().equals(Role.OWNER)) {
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
    public void forgetPassword(ForgetPasswordDTO forgetPasswordDTO, String resetToken) throws TokenExpired, MessagingException, PasswordDoesNotMatch, TokenNotFound {
      ForgetPassword forgetPassword = forgetPasswordRepo.findOneByToken(resetToken)
              .orElseThrow(() -> new TokenNotFound("Forget password link has been expired"));
       if(!forgetPassword.getExpiry().isBefore(LocalDateTime.now(ZoneOffset.UTC))){
          if(forgetPasswordDTO.newPassword().equals(forgetPasswordDTO.confirmPassword())){
              User user = forgetPassword.getUser();
              user.setPassword(passwordEncoder.encode(forgetPasswordDTO.confirmPassword()));
              user.setForgetPassword(null);
              userRepository.save(user);
              // send password changed successfully email
              emailService.PasswordChangedSuccessfully(user);
          } else {
              throw new PasswordDoesNotMatch("Password does not match");
          }
      }
       throw new TokenExpired("Link has been expired. Try again by requesting new link ");
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
                .expiry(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(3))
                .is_Active(true)
                .build();
    }

    public void verify(VerifyDTO verifyDTO) throws IncorrectEmail, TokenExpired, WrongVerificationCode {
      User user = userRepository.findByEmail(verifyDTO.email())
               .orElseThrow(() -> new IncorrectEmail("Invalid email. Try correct email"));
     if(!user.getVerification().getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))){
         if(user.getVerification().getOtpCode() == verifyDTO.code()){
             user.set_enabled(true);
             user.setVerification(null);
             userRepository.save(user);
         } else {
           throw new WrongVerificationCode("Wrong code entered");
         }
     } else {
         throw new TokenExpired("Verification token expired. Request new token again");
     }
    }

    public void resendEmail(String email, String emailType) throws IncorrectEmail, MessagingException {
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new IncorrectEmail("Incorrect email"));
       if(emailType.equals("VERIFICATION_CODE")){
           user.setVerification(createVerification(user));
           userRepository.save(user);
           emailService.resendVerificationEmail(user);
       } else if (emailType.equals("FORGET_PASSWORD_LINK")){
           user.setForgetPassword(createForgetPasswordToken(user));
           userRepository.save(user);
           emailService.ForgetPasswordLink(user);
       }
    }
    public User getUserByEmail(String email) throws UserNotFound {
       return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("Invalid email. User not found of this email."));
    }
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
