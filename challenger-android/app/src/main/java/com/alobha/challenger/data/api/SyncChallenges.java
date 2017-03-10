package com.alobha.challenger.data.api;

import com.alobha.challenger.data.entities.Challenge;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mrNRG on 28.06.2016.
 */
public class SyncChallenges implements Serializable {
    public String token;
    public List<Challenge> challenges;

    public SyncChallenges() {
    }

    public SyncChallenges(String token, List<Challenge> challenges) {
        this.token = token;
        this.challenges = challenges;
    }
}
