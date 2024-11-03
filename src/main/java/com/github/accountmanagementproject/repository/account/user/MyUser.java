package com.github.accountmanagementproject.repository.account.user;

import com.github.accountmanagementproject.repository.account.socialid.SocialId;
import com.github.accountmanagementproject.repository.account.user.myenum.Gender;
import com.github.accountmanagementproject.repository.account.user.myenum.UserStatus;
import com.github.accountmanagementproject.repository.account.user.role.Role;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.service.mapper.converter.GenderConverter;
import com.github.accountmanagementproject.service.mapper.converter.UserStatusConverter;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthSignUpRequest;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountInfoDto;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountSummary;
import com.github.accountmanagementproject.web.dto.account.mypage.TempInfoModifyForFigma;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@DynamicInsert
@Entity
@Table(name = "users")
@EqualsAndHashCode(of = "userId")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(unique = true, nullable = false, length = 30)
    private String nickname;

    @Column(name = "phone_number", unique = true, nullable = false, length = 15)
    private String phoneNumber;


    @Column(nullable = false)
    private String password;

    @Convert(converter = GenderConverter.class)
    @Column(length = 4)
    private Gender gender;

    @Column(name = "profile_message")
    private String profileMessage;

    @Column(name = "profile_img", nullable = false)
    private String profileImg;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Convert(converter = UserStatusConverter.class)
    @Column(length = 10)
    private UserStatus status;

    @Column(name = "failure_count", columnDefinition = "INT DEFAULT 0")
    private Integer failureCount;

    @Column(name = "withdrawal_date")
    private LocalDateTime withdrawalDate;

    @Column(name = "failure_date")
    private LocalDateTime failureDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)//⬅️기본값이 lazy 이기 때문에 굳이 명시적으로 작성할 필요는 없음
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),//참조할 fk
            inverseJoinColumns = @JoinColumn(name = "role_id"))//상대 엔티티에서 참조할 fk
    private Set<Role> roles;

    @OneToMany(mappedBy = "myUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)//orphanRemoval 고아제거
    private Set<SocialId> socialIds;

    @ManyToMany(mappedBy = "crewUsers")
    private List<Crew> crews;


    public boolean isLocked(){
        return this.status == UserStatus.LOCK;
    }

    public boolean isTempAccount(){
        return this.status == UserStatus.TEMP;
    }
    public boolean isDisabled(){
        return this.status == UserStatus.WITHDRAWAL || this.status == UserStatus.TEMP;
    }
    public boolean isExpired(){
        return lastLogin != null && this.lastLogin.isBefore(LocalDateTime.now().minusMonths(3));
    }
    public boolean isEnabled(){
        return this.status == UserStatus.NORMAL;
    }
    public boolean isCredentialsExpired(){
        return false;
    }
    public boolean isUnlockTime(){
        return this.failureDate != null
                && this.failureDate.isBefore(LocalDateTime.now().minusMinutes(5));
    }
    public boolean isFailureCountingOrLocking(){
        return this.failureCount < 4;
    }

    //로그인 실패 또는 성공시의 변화 될 값 DB에 반영
    public void loginValueSetting(boolean failure){
        //5번째 시도이고 5분이내 한번 더 시도했을시 잠금처리
        this.status = failure ?
                (isFailureCountingOrLocking()||isUnlockTime() ? UserStatus.NORMAL : UserStatus.LOCK)
                : UserStatus.NORMAL;
        //실패시 failureCount 를 1 증가시킨다. 단 계정이 잠길땐 0으로 만들고, 실패한지 5분 이상 지났을시 1부터 다시시작
        this.failureCount = failure ?
                (isUnlockTime() ?
                        1
                        : (isFailureCountingOrLocking() ? failureCount + 1 : 0))
                : 0;
        this.failureDate = failure ? LocalDateTime.now() : null;
        this.lastLogin = !failure ? LocalDateTime.now() : this.lastLogin;
    }

    public void oAuthSignUpSetting(OAuthSignUpRequest oAuthSignUpRequest){
        this.email = oAuthSignUpRequest.getEmail();
        this.nickname = oAuthSignUpRequest.getNickname();
        this.status = UserStatus.NORMAL;
        this.profileImg = oAuthSignUpRequest.getProfileImg();
        this.phoneNumber = oAuthSignUpRequest.getPhoneNumber();
        this.gender = oAuthSignUpRequest.getGender();
        if(oAuthSignUpRequest.getDateOfBirth() != null)
            this.dateOfBirth = LocalDate.parse(oAuthSignUpRequest.getDateOfBirth(), DateTimeFormatter.ofPattern("yyyy-M-d"));
    }

    public void withdrawalProcessing() {
        this.status = UserStatus.WITHDRAWAL;
        this.withdrawalDate = LocalDateTime.now();
    }

    public MyUser updateUserInfo(AccountInfoDto modifyRequest) {
        this.email = modifyRequest.getEmail();
        this.nickname = modifyRequest.getNickname();
        this.phoneNumber = modifyRequest.getPhoneNumber();
        this.gender = modifyRequest.getGender();
        this.dateOfBirth = modifyRequest.getDateOfBirth() == null ? null :
                LocalDate.parse(modifyRequest.getDateOfBirth(), DateTimeFormatter.ofPattern("yyyy-M-d"));
        this.profileImg = modifyRequest.getProfileImg();
        return this;
    }

    public void updateUserSummaryInfo(AccountSummary accountSummary) {
        if(accountSummary.getNickname() != null) this.nickname = accountSummary.getNickname();
        this.profileMessage = accountSummary.getProfileMessage() == null||accountSummary.getProfileMessage().isBlank()?
                null : accountSummary.getProfileMessage();
        this.profileImg = accountSummary.getProfileImg();
    }

    public void updateUserInfoForFigma(TempInfoModifyForFigma modifyRequest) {
        if(modifyRequest.getNickname()!=null) this.nickname = modifyRequest.getNickname();
        if(modifyRequest.getEmail()!=null) this.email = modifyRequest.getEmail();
        this.phoneNumber = modifyRequest.getPhoneNumber();
        this.profileImg = modifyRequest.getProfileImg();
    }
    //    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return this.userRoles.stream().map(userRole -> userRole.getRoles())
//                .map(role -> role.getName())

}