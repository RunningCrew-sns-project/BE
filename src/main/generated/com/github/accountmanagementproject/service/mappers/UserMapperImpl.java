package com.github.accountmanagementproject.service.mappers;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.web.dto.accountAuth.AccountDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-28T04:43:54+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.8.jar, environment: Java 17.0.11 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy년_M월_d일_0705652203 = DateTimeFormatter.ofPattern( "yyyy년 M월 d일" );
    private final DateTimeFormatter dateTimeFormatter_yyyy_M_d_0277261865 = DateTimeFormatter.ofPattern( "yyyy-M-d" );

    @Override
    public AccountDto myUserToAccountDto(MyUser myUser) {
        if ( myUser == null ) {
            return null;
        }

        AccountDto accountDto = new AccountDto();

        accountDto.setRoles( myRoles( myUser.getRoles() ) );
        accountDto.setGender( myUser.getGender() );
        if ( myUser.getDateOfBirth() != null ) {
            accountDto.setDateOfBirth( dateTimeFormatter_yyyy년_M월_d일_0705652203.format( myUser.getDateOfBirth() ) );
        }
        accountDto.setEmail( myUser.getEmail() );
        accountDto.setPassword( myUser.getPassword() );
        accountDto.setNickname( myUser.getNickname() );
        accountDto.setPhoneNumber( myUser.getPhoneNumber() );
        accountDto.setProfileImg( myUser.getProfileImg() );
        if ( myUser.getLastLogin() != null ) {
            accountDto.setLastLogin( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( myUser.getLastLogin() ) );
        }
        accountDto.setStatus( myUser.getStatus() );

        return accountDto;
    }

    @Override
    public MyUser accountDtoToMyUser(AccountDto accountDto) {
        if ( accountDto == null ) {
            return null;
        }

        MyUser myUser = new MyUser();

        if ( accountDto.getDateOfBirth() != null ) {
            myUser.setDateOfBirth( LocalDate.parse( accountDto.getDateOfBirth(), dateTimeFormatter_yyyy_M_d_0277261865 ) );
        }
        myUser.setEmail( accountDto.getEmail() );
        myUser.setNickname( accountDto.getNickname() );
        myUser.setPhoneNumber( accountDto.getPhoneNumber() );
        myUser.setPassword( accountDto.getPassword() );
        myUser.setGender( accountDto.getGender() );
        myUser.setProfileImg( accountDto.getProfileImg() );
        if ( accountDto.getLastLogin() != null ) {
            myUser.setLastLogin( LocalDateTime.parse( accountDto.getLastLogin() ) );
        }
        myUser.setStatus( accountDto.getStatus() );

        return myUser;
    }
}
