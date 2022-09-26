package com.project.raidho.domain.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    public void updateTokenValue(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
