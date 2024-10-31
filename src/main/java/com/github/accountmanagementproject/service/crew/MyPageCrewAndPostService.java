package com.github.accountmanagementproject.service.crew;


import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageCrewAndPostService {
    private final CrewsRepository crewsRepository;

    public List<MyCrewResponse> getMyCrew(String email, Boolean isRequesting){
//        return crewsRepository.findMyCrewsByEmail(email, isRequesting);
        return null;
    }

}
