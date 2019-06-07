package kr.co.area.hashtag.myPage;

public class MyFavoriteListViewItem {
    private String img;
    private String restName;
    private String address;
    private String score;
    private String restId;

    public void setimg(String img) {
        this.img = img;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getImg() {
        return img;
    }

    public String getRestName() {
        return restName;
    }

    public String getAddress() {
        return address;
    }

    public String getScore() {
        return score;
    }

    public String getRestId(){
        return restId;
    }
}
