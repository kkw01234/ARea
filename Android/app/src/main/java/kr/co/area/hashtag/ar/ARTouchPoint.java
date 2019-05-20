package kr.co.area.hashtag.ar;

public class ARTouchPoint {
    float x, y;
    String restID;

    public ARTouchPoint(String restID, float x, float y) {
        this.x = x;
        this.y = y;
        this.restID = restID;
    }

    boolean isInTouchPoint(float touchX, float touchY){ // 여분의 길이를 얼마로 둘것인지...
        if(x <= touchX && touchX <= x + 250 && y <= touchY && touchY <= y + 350) return true;
        return false;
    }
}
