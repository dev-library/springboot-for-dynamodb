package com.example.dynamodbdemo.music.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "MusicCollection")
public class MusicCollection {

    @DynamoDBHashKey
    private String artist;

    @DynamoDBRangeKey
    private String songTitle;

    @DynamoDBAttribute
    private String albumTitle;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "album-index")
    private String albumTitleIndex;

    @DynamoDBAttribute
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "album-index")
    private String releaseYear;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
        this.albumTitleIndex = albumTitle; // GSI용 필드 동기화
    }

    public String getAlbumTitleIndex() {
        return albumTitleIndex;
    }

    public void setAlbumTitleIndex(String albumTitleIndex) {
        this.albumTitleIndex = albumTitleIndex;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }
}
