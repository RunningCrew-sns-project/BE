package com.github.accountmanagementproject.web.dto.account.mypage;

import com.github.accountmanagementproject.web.dto.account.DtoDefaultProfileImageSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountModifyRequest extends AccountInfoDto implements DtoDefaultProfileImageSetting {
}
