package com.github.accountmanagementproject.config.security;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
import com.github.accountmanagementproject.repository.account.users.enums.RolesEnum;
import com.github.accountmanagementproject.repository.account.users.roles.Role;
import com.github.accountmanagementproject.repository.account.users.roles.RolesJpa;
import com.github.accountmanagementproject.service.customExceptions.CustomBadRequestException;
import com.github.accountmanagementproject.service.customExceptions.CustomNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountConfig {
    private final RolesJpa rolesJpa;
    private final MyUsersJpa myUsersJpa;
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
        if(emailOrPhoneNumber.matches("01\\d{9}")){
            return myUsersJpa.findByPhoneNumberJoin(emailOrPhoneNumber).orElseThrow(()->
                    new CustomNotFoundException.ExceptionBuilder()
                            .customMessage("가입되지 않은 핸드폰 번호")
                            .request(emailOrPhoneNumber)
                            .build());
        }else if (emailOrPhoneNumber.matches(".+@.+\\..+")){
            return myUsersJpa.findByEmailJoin(emailOrPhoneNumber).orElseThrow(()->
                    new CustomNotFoundException.ExceptionBuilder()
                            .customMessage("가입되지 않은 이메일")
                            .request(emailOrPhoneNumber)
                            .build());
        }else throw new CustomBadRequestException.ExceptionBuilder()
                .customMessage("잘못 입력된 식별자")
                .request(emailOrPhoneNumber)
                .build();
    }
    public MyUser findMyUser(String emailOrPhoneNumber){
        if(emailOrPhoneNumber.matches("01\\d{9}")){
            return myUsersJpa.findByPhoneNumber(emailOrPhoneNumber).orElseThrow(()->
                    new CustomNotFoundException.ExceptionBuilder()
                            .customMessage("가입되지 않은 핸드폰 번호")
                            .request(emailOrPhoneNumber)
                            .build());
        }else if (emailOrPhoneNumber.matches(".+@.+\\..+")){
            return myUsersJpa.findByEmail(emailOrPhoneNumber).orElseThrow(()->
                    new CustomNotFoundException.ExceptionBuilder()
                            .customMessage("가입되지 않은 이메일")
                            .request(emailOrPhoneNumber)
                            .build());
        }else throw new CustomBadRequestException.ExceptionBuilder()
                .customMessage("잘못 입력된 식별자")
                .request(emailOrPhoneNumber)
                .build();
    }






    @Transactional
    public MyUser failureCounting() {
        MyUser failUser = (MyUser) httpSession.getAttribute("myUser");
//        MyUser failUser = findMyUser(principal);
//        MyUser findUser = entityManager.find(MyUser.class, failUser.getUserId());
//        failUser = entityManager.merge(failUser);
        if (failUser != null) {
            failUser = entityManager.find(MyUser.class, failUser.getUserId());
        }
        failUser.loginValueSetting(true);
        return failUser;
    }

    @Transactional
    public void loginSuccessEvent(String principal) {
        MyUser sucUser = findMyUser(principal);
        sucUser.loginValueSetting(false);
    }

}
