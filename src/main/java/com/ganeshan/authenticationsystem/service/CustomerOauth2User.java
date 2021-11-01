package com.ganeshan.authenticationsystem.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomerOauth2User implements OAuth2User {

    private final List<GrantedAuthority> authorities =
            AuthorityUtils.createAuthorityList("USER");
    private OAuth2User oAuth2User;

    public CustomerOauth2User(OAuth2User user) {
        this.oAuth2User = user;
    }

    //    added custom authorities
    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getEmail() {
        return oAuth2User.<String>getAttribute("email");
    }
}
