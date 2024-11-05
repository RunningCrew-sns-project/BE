package com.github.runningcrewsnsproject.repository.account.socialid;

import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.account.user.myenum.OAuthProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_ids")
@DynamicInsert
@NoArgsConstructor
@Getter
@Setter
public class SocialId {

   @EmbeddedId
   private SocialIdPk socialIdPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    private LocalDateTime connectAt;


    public SocialId(String socialId, OAuthProvider provider) {
        this.socialIdPk = new SocialIdPk(socialId, provider);
    }
    public void socialConnectSetting(){
        this.connectAt = LocalDateTime.now();
    }

}
