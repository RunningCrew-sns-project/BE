package com.github.runningcrewsnsproject.service.account.auth.oauth;

import com.github.runningcrewsnsproject.config.client.OAuthApiClient;
import com.github.runningcrewsnsproject.config.client.dto.userInfo.OAuthUserInfo;
import com.github.runningcrewsnsproject.exception.CustomBadRequestException;
import com.github.runningcrewsnsproject.repository.account.user.myenum.OAuthProvider;
import com.github.runningcrewsnsproject.web.dto.account.auth.oauth.request.OAuthLoginParams;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthClientManager {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    //OAuthApiClient를 상속받은 클라이언트들을 전부 불러와 Map으로 만들어준다.
    public OAuthClientManager(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );//key 값에 OAuthProvider 값을 넣어주고 value 값에는 OAuthApiClient 인스턴스 자체를 값으로 사용
    }

    public OAuthUserInfo request(OAuthLoginParams params) {
        //get요청으로 받아온 OAuthProvider에 해당하는 클라이언트를 가져온다.
        OAuthApiClient client = clients.get(params.oAuthProvider());
        try {
            String accessToken = client.requestAccessToken(params);
            return client.requestOauthInfo(accessToken);
        }catch (HttpClientErrorException ex){
            throw new CustomBadRequestException.ExceptionBuilder()
                    .systemMessage(ex.getMessage())
                    .customMessage("전달된 code가 만료되었습니다.")
                    .request(params.makeBody().getFirst("code"))
                    .build();
        }
    }
}
