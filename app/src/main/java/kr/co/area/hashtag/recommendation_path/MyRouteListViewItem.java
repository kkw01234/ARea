package kr.co.area.hashtag.recommendation_path;

public class MyRouteListViewItem {
    private String restId;
    private String restName;
    private int sequence;

    public String getRestId(){
        return this.restId;
    }

    public String getRestName(){
        return this.restName;
    }

    public int getSequence(){
        return this.sequence;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

}
