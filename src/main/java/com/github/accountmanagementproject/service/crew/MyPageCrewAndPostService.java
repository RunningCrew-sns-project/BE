package com.github.accountmanagementproject.service.crew;


import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageCrewAndPostService {
    private final CrewsUsersRepository crewsUsersRepository;
    private final CrewsRepository crewsRepository;

    @Transactional(readOnly = true)
    public List<MyCrewResponse> getMyCrew(String email, Boolean isAll){
        List<MyCrewResponse> myCrewResponses = crewsUsersRepository.findMyCrewResponseByEmail(email, isAll);
        if(isAll!=null&&!isAll) return myCrewResponses;
        List<MyCrewResponse> crewIMade = crewsRepository.findIMadeCrewResponseByEmail(email);
        crewIMade.addAll(myCrewResponses);
        return crewIMade;
    }
}
