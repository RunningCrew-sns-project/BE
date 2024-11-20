package com.github.accountmanagementproject.web.dto.account.mypage;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.github.accountmanagementproject.repository.account.user.myenum.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountInfoDto {
    @NotBlank(message = "이메일은 필수 입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    @Schema(description = "이메일", example = "abc@abc.com", pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    @Pattern(regexp = "^(?!01\\d{9}$).*$", message = "핸드폰 번호를 닉네임으로 사용할 수 없습니다.")
    @Schema(description = "닉네임", example = "이브라히모비치",  minLength = 2, maxLength = 8)
    private String nickname;

    @NotBlank(message = "핸드폰 번호는 필수 입니다.")
    @Pattern(regexp = "01\\d{9}", message = "핸드폰 번호는 01로 시작하며 11자리 숫자여야 합니다.")
    @Schema(description = "핸드폰 번호", example = "01012345678", pattern = "01\\d{9}", minLength = 11, maxLength = 11)
    private String phoneNumber;

    @Schema(description = "성별", example = "남성", defaultValue = "미정")
    @JsonSetter(nulls = Nulls.SKIP)
    private Gender gender = Gender.UNKNOWN;

    @Pattern(regexp= "^(19|20)\\d{2}-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])$|" +
            "^(19|20)\\d{2}-(02)-(29)$|" +
            "^(19|20)\\d{2}-(0?[1,3-9]|1[0-2])-(30)$|" +
            "^(19|20)\\d{2}-(0?[13578]|1[0,2])-(31)$",
            message = "생년월일은 yyyy-M-d 형식으로 입력해주세요.")
    @Schema(description = "생년월일", example = "1999-1-8", pattern = "^(19|20)\\d{2}-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])$|" +
            "^(19|20)\\d{2}-(02)-(29)$|" +
            "^(19|20)\\d{2}-(0?[1,3-9]|1[0-2])-(30)$|" +
            "^(19|20)\\d{2}-(0?[13578]|1[0,2])-(31)$")
    private String dateOfBirth;

    @Pattern(regexp = "^http.*$",
            message = "프로필 이미지는 이미지 링크의 URL 로 요청을 보내주세요.")
    @Schema(description = "프로필 이미지 (요청 값에 포함 시키지 않을 시 기본 사진이 적용 됩니다.)",
            defaultValue = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/elderly-man-icon.png",
            example = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/elderly-man-icon.png", pattern = "^(http(s)?://)[\\w.-]+(/[\\w+.%&=;:@#\\-]*)*\\.(jpg|jpeg|png|gif|bmp|svg|JPG|JPEG|PNG|GIF|BMP|SVG)$")
    private String profileImg;



//
//    public boolean requiredNull(){
//        return this.email==null
//                ||this.password==null
//                ||this.passwordConfirm==null
//                ||this.nickname==null
//                ||this.phoneNumber==null;
//    }
//    public boolean badEmailValue(){
//        return !this.email.matches(".+@.+\\..+");
//    }
//    public boolean badPhoneNumValue(){
//        return !this.phoneNumber.matches("01\\d{9}");
//    }
//    public boolean badNicknameValue(){
//        return this.getNickname().matches("01\\d{9}");
//    }
//    public boolean badPasswordValue(){
//        return !this.password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
//    }
//    public boolean differentPasswordConfirm(){
//        return !this.passwordConfirm.equals(this.password);
//    }

}
