package kr.co.area.hashtag.ar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.AltitudeTask;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

/**
 * Created by ntdat on 1/13/17.
 */

public class AROverlayView extends View implements PlacesListener {
    ARActivity activity;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location psLocation;
    private Location currentLocation;
    private List<ARPoint> arPoints;
    private List<ARTouchPoint> arTouchPoints;
    private Bitmap bmp; // 식당 이미지 by 비트맵
    private int meter;

    public AROverlayView(Activity activity, int meter) {
        super(activity);
        this.activity = (ARActivity) activity;
        bmp = scaleDown(BitmapFactory.decodeResource(getResources(), R.drawable.ar_rest), 200, true); // 이미지 축소시키기
        arPoints = new ArrayList<>();
        arTouchPoints = new ArrayList<>();
        this.meter = meter;
    }

    public void touch(float x, float y){
        for(int i = 0; i < arTouchPoints.size() ; ++i){
            ARTouchPoint arTouchPoint = arTouchPoints.get(i);
            if(arTouchPoint.isInTouchPoint(x, y)) {
                activity.requestRestInfo(arTouchPoint);
                return;
            }
        }
        // Toast.makeText(activity, "x : " + x + ", y : " + y, Toast.LENGTH_SHORT).show();
    }

    public void setMeter(int meter) {
        this.meter = meter;
        doSearch();
    }

    public void pause() {
        arPoints.clear();
    }

    public void resume() {
        doSearch();
    }

    public void doSearch() {
        if (currentLocation == null) return;
        psLocation = currentLocation;
        new NRPlaces.Builder()
                .listener(this)
                .key(getResources().getString(R.string.google_maps_key))
                .latlng(currentLocation.getLatitude(), currentLocation.getLongitude())//현재 위치
                .radius(meter) // 반경 param 미터 내에서 검색
                .type(PlaceType.RESTAURANT) // TYPE : 음식점
                .build()
                .execute();
    }


    public Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) { // 사진 축소 함수
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate(); // onDraw 요청
    }

    public void updateCurrentLocation(Location currentLocation) {
        if (psLocation == null || currentLocation.distanceTo(psLocation) >= 15) // 이동 거리가 15m 이상일 때 구글 플레이스 서치
            doSearch();
        this.currentLocation = currentLocation; // location 갱신
        this.invalidate();
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
    }

    @Override
    public void onPlacesStart() {
        arPoints.clear();
    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        activity.runOnUiThread(() -> {
            if (places != null) {
                for (Place place : places) {
                    LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    ARPoint arPoint = new ARPoint(place.getPlaceId(), place.getName(), latLng.latitude, latLng.longitude);
                    arPoints.add(arPoint);
                }
            }
        });
    }

    @Override
    public void onPlacesFinished() {
    }

    @Override
    protected void onDraw(Canvas canvas) { // 주기적으로 화면 그리기(마커 띄우기)
        super.onDraw(canvas);

        if (currentLocation == null) { // 현재 위치 정보를 받을 수 없다면
            return; // 그리지 않는다
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 마커 글자 스타일 설정 위한 paint
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(50);
        arTouchPoints.clear(); // 터치 리셋

        for (int i = 0; i < arPoints.size(); i++) { //  각각을 계산해서 현재 화면에 맞게 그려주기
            ARPoint arPoint = arPoints.get(i);
            arPoint.getLocation().setAltitude(currentLocation.getAltitude());
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoint.getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) { // 화면에 보여져야 할 위치에 있는 식당이라면,
                float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();

                canvas.drawBitmap(bmp, x - bmp.getWidth() / 2, y - bmp.getHeight() / 2, null); // 식당 아이콘과
                String name = arPoint.getName();
                if(name.length() > 5) name = name.substring(0, 4) + "..";

                canvas.drawText(name, x - 20 * name.length(), y + bmp.getHeight() / 2 + 50, paint); // 식당 이름을 그린다

                arTouchPoints.add(new ARTouchPoint(arPoint.getID(), x - bmp.getWidth() / 2, y - bmp.getHeight() / 2)); // 터치 포인트 추가
            }
        }
    }
}
