package com.github.accountmanagementproject.web.dto.accountAuth.myPage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountMain {
    private String nickname;
    private String profileImg;
    private String profileMessage;
    private MyCrewResponse myCrewResponse;
    private MyRecords myRecords;
}
