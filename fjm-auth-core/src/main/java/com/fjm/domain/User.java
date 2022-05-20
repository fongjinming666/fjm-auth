package com.fjm.domain;

import com.fjm.entity.OauthUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 下午5:29
 * @Description:
 */
@Data
@NoArgsConstructor
public class User extends OauthUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    /**
     * 当前用户对应clientId
     */
    private String clientId;

    /**
     * 微信openId
     */
    private String openId;

    /**
     * 苹果用户Id
     */
    private String appleUserId;

    private Collection<GrantedAuthority> authorities;

    /**
     * 构造器
     *
     * @param oauthUser
     */
    public User(OauthUser oauthUser) {
        super(oauthUser.getId(), oauthUser.getUsername(), oauthUser.getFullname(), oauthUser.getGender(), oauthUser.getPassword(), oauthUser.getOriginPwd(),
                oauthUser.getSmsCode(), oauthUser.getPhone(), oauthUser.getSource(),  oauthUser.getCreateTime(),
                oauthUser.getModifyTime(), oauthUser.getStatus(), oauthUser.getDeleted());
    }

    /**
     * 构造器
     *
     * @param clientId
     * @param oauthUser
     */
    public User(String clientId, OauthUser oauthUser) {
        super(oauthUser.getId(), oauthUser.getUsername(), oauthUser.getFullname(), oauthUser.getGender(), oauthUser.getPassword(), oauthUser.getOriginPwd(),
                oauthUser.getSmsCode(), oauthUser.getPhone(), oauthUser.getSource(),  oauthUser.getCreateTime(),
                oauthUser.getModifyTime(), oauthUser.getStatus(), oauthUser.getDeleted());
        this.clientId = clientId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
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
        if (super.getStatus() == null) {
            return false;
        }
        return super.getStatus() == 1 ? true : false;
    }
}
