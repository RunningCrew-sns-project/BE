package com.github.accountmanagementproject.web.dto.crews;

import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CrewCreationRequest {
    @NotBlank(message = "크루 이름은 필수입니다.")
    private String crewName;
    @NotBlank(message = "크루 소개는 필수입니다.")
    private String crewIntroduction;
    @Schema(description = "크루 이미지 URL", example = """
            [
                {
                    "url": "https://wwww.aasdfjlzxvc.czz/sdfa.png"
                },
                {
                    "url": "https://wwww.aasdfjlzxvc.czz/sdfa.png"
                },
                {
                    "url": "https://wwww.aasdfjlzxvc.czz/sdfa.png"
                }
            ]""")
    private List<UrlDto> crewImageUrls;
    @Schema(description = "첨부 파일 정보", example = """
            [
                {
                    "fileName": "첨부파일이름1.png",
                    "fileUrl": "1111.url"
                },
                {
                    "fileName": "첨부파일이름2.png",
                    "fileUrl": "2222.url"
                },
                {
                    "fileName": "첨부파일이름2.png",
                    "fileUrl": "3333.url"
                },
                {
                    "fileName": "첨부파일이름3.png",
                    "fileUrl": "4444.url"
                },
                {
                    "fileName": "5555.png",
                    "fileUrl": "5555.url"
                }
            ]""")
    private List<FileDto> fileDtos;
    @NotBlank(message = "크루 활동 지역은 필수입니다.")
    private String activityRegion;
    @NotNull(message = "크루 최대 인원 설정은 필수입니다.")
    private Integer maxCapacity;
}
