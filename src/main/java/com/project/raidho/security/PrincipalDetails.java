package com.project.raidho.security;


import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class PrincipalDetails implements UserDetails {

    private final Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    // 해당 회원의 권한을 Return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority GrantedAuthority = new SimpleGrantedAuthority(MemberRole.USER.toString());
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(GrantedAuthority);
        return collection;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getMemberName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
