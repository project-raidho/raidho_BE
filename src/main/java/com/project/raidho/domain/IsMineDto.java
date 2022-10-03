package com.project.raidho.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class IsMineDto {
    private boolean isMine = false;
    private boolean isHeartMine = false;
    private boolean isAlreadyMine = false;
    private boolean isStarMine = false;
    private int heartCount = 0;
    private int commentCount = 0;
}
