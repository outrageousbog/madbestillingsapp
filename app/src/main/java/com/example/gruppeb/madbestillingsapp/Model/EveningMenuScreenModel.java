package com.example.gruppeb.madbestillingsapp.Model;

public class EveningMenuScreenModel {

    private String eveningName, eveningDescription, eveningComment;

    public EveningMenuScreenModel(String eveningName, String eveningDescription, String eveningComment) { //int eveningImageView) {
        this.eveningName = eveningName;
        this.eveningDescription = eveningDescription;
        this.eveningComment = eveningComment;
    }

    public String getEveningName() {
        return eveningName;
    }

    public String getEveningDescription() {
        return eveningDescription;
    }

    public String getEveningComment() {
        return eveningComment;
    }


}
