package com.github.runningcrewsnsproject.service.crew;


import com.github.runningcrewsnsproject.repository.crew.crew.Crew;
import com.github.runningcrewsnsproject.repository.crew.crew.CrewsRepository;
import com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsers;
import com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersPk;
import com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.runningcrewsnsproject.service.mapper.crew.CrewMapper;
import com.github.runningcrewsnsproject.web.dto.crew.MyCrewResponse;
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
        List<CrewsUsers> entityList = new ArrayList<>();

        if(isAll==null||isAll)//요청중인 크루만 조회시 내가만든 크루는 제외
            entityList.addAll(theCrewIMade.stream()
                    .map(crew -> new CrewsUsers(new CrewsUsersPk(crew, crew.getCrewMaster()))).toList());

        entityList.addAll(crewsUsersRepository.findMyCrewsByEmail(email, isAll));

        return entityList.stream().
                map(CrewMapper.INSTANCE::crewsUsersToMyCrewResponse).toList();
    }

}
