package com.github.runningcrewsnsproject.config.security;

import com.github.runningcrewsnsproject.exception.CustomBadRequestException;
import com.github.runningcrewsnsproject.exception.CustomNotFoundException;
import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.account.user.MyUsersRepository;
import com.github.runningcrewsnsproject.repository.account.user.myenum.RolesEnum;
import com.github.runningcrewsnsproject.repository.account.user.role.Role;
import com.github.runningcrewsnsproject.repository.account.user.role.RolesJpa;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountConfig {
    private static final Logger log = LoggerFactory.getLogger(AccountConfig.class);
    private final RolesJpa rolesJpa;
    private final MyUsersRepository myUsersRepository;
    private final EntityManager entityManager;
    private final HttpSession httpSession;


    //일반유저롤 자주 호출되서 싱글톤적용
    private static Role normalUserRole;

    private static Role adminUserRole;

//    @PostConstruct
//    public void init(){
//        normalUserRole = rolesJpa.findByName("ROLE_USER");
//    }

    public Role getNormalUserRole(){
        if(normalUserRole == null){
            normalUserRole = rolesJpa.findByName(RolesEnum.ROLE_USER);
        }
        System.out.println(normalUserRole.getName());
        return normalUserRole;
    }
    public Role getAdminUserRole(){
        if(adminUserRole == null){
            adminUserRole = rolesJpa.findByName(RolesEnum.ROLE_ADMIN);
        }
        return adminUserRole;
    }



    public MyUser findMyUserFetchJoin(String emailOrPhoneNumber){
        CustomNotFoundException.ExceptionBuilder exceptionBuilder = new CustomNotFoundException.ExceptionBuilder().request(emailOrPhoneNumber);
        if(emailOrPhoneNumber.matches("01\\d{9}")){
            return myUsersRepository.findByPhoneNumberJoin(emailOrPhoneNumber).orElseThrow(()->
                    exceptionBuilder.customMessage("가입되지 않은 핸드폰 번호").build());
        }else if (emailOrPhoneNumber.matches(".+@.+\\..+")){
            return myUsersRepository.findByEmailJoin(emailOrPhoneNumber).orElseThrow(()->
                    exceptionBuilder.customMessage("가입되지 않은 이메일").build());
        }else throw new CustomBadRequestException.ExceptionBuilder()
                .customMessage("잘못 입력된 식별자")
                .request(emailOrPhoneNumber)
                .build();
    }
    public MyUser findMyUser(String emailOrPhoneNumber){
        CustomNotFoundException.ExceptionBuilder exceptionBuilder = new CustomNotFoundException.ExceptionBuilder().request(emailOrPhoneNumber);
        if(emailOrPhoneNumber.matches("01\\d{9}")){
            return myUsersRepository.findByPhoneNumber(emailOrPhoneNumber).orElseThrow(()->
                    exceptionBuilder.customMessage("가입되지 않은 핸드폰 번호").build());
        }else if (emailOrPhoneNumber.matches(".+@.+\\..+")){
            return myUsersRepository.findByEmail(emailOrPhoneNumber).orElseThrow(()->
                    exceptionBuilder.customMessage("가입되지 않은 이메일").build());
        }else throw new CustomBadRequestException.ExceptionBuilder()
                .customMessage("잘못 입력된 식별자")
                .request(emailOrPhoneNumber)
                .build();
    }






    @Transactional
    public MyUser failureCounting(String email) {
        MyUser failUser = findMyUser(email);
        failUser.loginValueSetting(true);
        return failUser;
    }

    @Transactional
    public void loginSuccessEvent(String principal) {
        MyUser sucUser = findMyUser(principal);
        sucUser.loginValueSetting(false);
    }

}
