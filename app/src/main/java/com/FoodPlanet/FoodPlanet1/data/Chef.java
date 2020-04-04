package com.FoodPlanet.FoodPlanet1.data;

public class Chef {

    private String chefName;
    private String chefEmail;
    private String chefPic;
    public String chefId;

    public String getChefName() {
        return chefName;
    }

    public void setChefName(String chefName) {
        this.chefName = chefName;
    }

    public String getChefEmail() {
        return chefEmail;
    }

    public void setChefEmail(String chefEmail) {
        this.chefEmail = chefEmail;
    }

    public String getChefPic() {
        return chefPic;
    }

    public void setChefPic(String chefPic) {
        this.chefPic = chefPic;
    }

    public String getChefId() {
        return chefId;
    }

    public void setChefId(String chefId) {
        this.chefId = chefId;
    }

    public Chef() {
    }

    public Chef(String chefId, String chefName, String chefEmail, String chefPic) {
        this.chefId = chefId;
        this.chefName = chefName;
        this.chefEmail = chefEmail;
        this.chefPic = chefPic;
    }
}
