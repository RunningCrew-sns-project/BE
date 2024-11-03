package com.github.accountmanagementproject.service.crew;


import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageCrewAndPostService {
    private final CrewsRepository crewsRepository;
    private final CrewsUsersRepository crewsUsersRepository;

    public List<MyCrewResponse> getMyCrew(String email, Boolean isAll){
        List<CrewsUsers> crewResponses = crewsUsersRepository.findMyCrewsByEmail(email, isAll);
        return null;
    }

}
