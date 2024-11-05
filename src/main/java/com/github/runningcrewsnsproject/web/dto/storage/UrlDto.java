package com.github.runningcrewsnsproject.web.dto.storage;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
public class UrlDto {
    @NotBlank(message = "url은 필수값입니다.")
    @URL(message = "url 형식이 올바르지 않습니다.")
    private String url;
}
