package com.github.accountmanagementproject.service.crew;


import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.service.mapper.crew.CrewMapper;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageCrewAndPostService {
    private final CrewsUsersRepository crewsUsersRepository;

    public List<MyCrewResponse> getMyCrew(String email, Boolean isAll){
        List<CrewsUsers> theCrewIMade = crewsUsersRepository.findIMadeCrewsByEmail(email);
        List<CrewsUsers> crewIJoined = crewsUsersRepository.findMyCrewsByEmail(email, isAll);

        List<MyCrewResponse> response = new ArrayList<>();
        if(isAll==null||isAll)//요청중인 크루만 조회시 내가만든 크루는 제외
            response.addAll(CrewMapper.INSTANCE.crewsUsersListToMyCrewResponseList(theCrewIMade, true));

        response.addAll(CrewMapper.INSTANCE.crewsUsersListToMyCrewResponseList(crewIJoined, false));

        return response;
    }

}
