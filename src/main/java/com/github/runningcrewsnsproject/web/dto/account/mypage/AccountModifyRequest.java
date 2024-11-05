package com.github.runningcrewsnsproject.web.dto.account.mypage;

import com.github.runningcrewsnsproject.web.dto.account.DtoDefaultProfileImageSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountModifyRequest extends AccountInfoDto implements DtoDefaultProfileImageSetting {
}
