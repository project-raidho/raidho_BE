package com.project.raidho.domain.oauthMemberInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OauthMemberInfoImpl {

    // Todo :: 추후 OauthMemberInfoDto interface 로 refactor
    private String memberName;
    private String email;
    private String provider;
    private String providerId;
    public OauthMemberInfoImpl(String memberName, String email, String providerId, String provider) {
        this.memberName = memberName;
        this.email = email;
        this.providerId = providerId;
        this.provider = provider;
    }
}
