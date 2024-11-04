package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CrewSecurityService {

    private final CrewsUsersRepository crewsUsersRepository;

    public Object isUserInCrew(Authentication authentication, Long crewId) {

        Long userId = ((MyUser) authentication.getPrincipal()).getUserId();

//        return crewsUsersRepository.existsByCrewIdAndCrewMasterId(crewId, userId);
        return null;
    }
}
