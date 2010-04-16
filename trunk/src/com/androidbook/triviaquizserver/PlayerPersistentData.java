/*
 * Copyright (c) 2009, Lauren Darcey and Shane Conder
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following disclaimer.
 *   
 * * Redistributions in binary form must reproduce the above copyright notice, this list 
 *   of conditions and the following disclaimer in the documentation and/or other 
 *   materials provided with the distribution.
 *   
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific prior 
 *   written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.androidbook.triviaquizserver;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class PlayerPersistentData {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String uniqueId;

    @Persistent
    private String nickname;

    @Persistent
    private String email;

    @Persistent
    private String password;

    @Persistent
    private Date birthdate;

    @Persistent
    private String gender;

    @Persistent
    private String favoritePlace;

    @Persistent
    private Long score;

    @Persistent
    private Blob avatar;

    // app managed unowned connections

    // people this player "follows"
    @Persistent
    private Set<Long> friends;

    // people that "follow" this player
    @Persistent
    private Set<Long> followers;

    public PlayerPersistentData(String uniqueId, String nickname, String email, String password, Date birthdate, String gender, String favoritePlace, Long score) {
        super();
        this.uniqueId = uniqueId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.gender = gender;
        this.favoritePlace = favoritePlace;
        this.score = score;
    }

    public Blob getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }

    public String getAvatarUrl() {
        return "/pi?key=" + getId();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFavoritePlace() {
        return favoritePlace;
    }

    public void setFavoritePlace(String favoritePlace) {
        this.favoritePlace = favoritePlace;
    }

    public void addFriend(PlayerPersistentData friend) {
        if (friends == null) {
            friends = new HashSet<Long>();
        }
        friends.add(friend.getId());
        friend.addFollower(this);
    }

    public void addFollower(PlayerPersistentData follower) {
        if (followers == null) {
            followers = new HashSet<Long>();
        }
        followers.add(follower.getId());
    }

    public void removeFriend(PlayerPersistentData friend) {
        if (friends != null) {
            friends.remove(friend.getId());
            friend.removeFollower(this);
        }
    }

    public void removeFollower(PlayerPersistentData follower) {
        if (followers != null) {
            followers.remove(follower.getId());
        }
    }

    public Set<Long> getFriends() {
        return friends;
    }

    public Set<Long> getFollowers() {
        return followers;
    }

    public Long getId() {
        return id;
    }

}
