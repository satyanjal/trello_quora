package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_auth", schema = "quora")
@NamedQueries(
        {
//                Enter you named queries like in UserEntity.java file
        }
)

public class UserAuthEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "access_token")
    @Size(max = 500)
    private String accessToken;

    @Column(name = "expires_at")
    @NotNull
    private Date expiresAt;

    @Column(name = "login_at")
    @NotNull
    private Date loginAt;

    @Column(name = "logout_at")
    @NotNull
    private Date logoutAt;

    public Integer getId() { return id; }

    public void setId(Integer id) {this.id = id;}

    public String getUuid() {return uuid;}

    public void setUuid(String uuid) {this.uuid = uuid;}

    public UserEntity getUser() {return user;}

    public void setUser(UserEntity user) {this.user = user;}

    public String getAccessToken() {return accessToken;}

    public void setAccessToken(String accessToken) {this.accessToken = accessToken;}

    public Date getExpiresAt() {return expiresAt;}

    public void setExpiresAt(Date expiresAt) {this.expiresAt = expiresAt;}

    public Date getLoginAt() {return loginAt;}

    public void setLoginAt(Date loginAt) {this.loginAt = loginAt;}

    public Date getLogoutAt() {return logoutAt;}

    public void setLogoutAt(Date logoutAt) {this.logoutAt = logoutAt;}

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

}
