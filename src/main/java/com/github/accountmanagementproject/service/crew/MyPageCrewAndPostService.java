package com.github.accountmanagementproject.service.crew;


import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersPk;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.service.mapper.crew.CrewMapper;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageCrewAndPostService {
    private final CrewsUsersRepository crewsUsersRepository;
    private final CrewsRepository crewsRepository;

    @Transactional(readOnly = true)
    public List<MyCrewResponse> getMyCrew(String email, Boolean isAll){
        List<Crew> theCrewIMade = crewsRepository.findIMadeCrewsByEmail(email);
        List<CrewsUsers> crewIJoined = crewsUsersRepository.findMyCrewsByEmail(email, isAll);
        List<CrewsUsers> theCrewIMadeUsers = theCrewIMade.stream()
                .map(crew -> new CrewsUsers(new CrewsUsersPk(crew, crew.getCrewMaster()))).toList();


        List<MyCrewResponse> response = new ArrayList<>();
        if(isAll==null||isAll)//요청중인 크루만 조회시 내가만든 크루는 제외
            response.addAll(CrewMapper.INSTANCE.crewsUsersListToMyCrewResponseList(theCrewIMadeUsers));

        response.addAll(CrewMapper.INSTANCE.crewsUsersListToMyCrewResponseList(crewIJoined));

        return response;
    }

}
