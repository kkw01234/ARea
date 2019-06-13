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
    private int meter;
    private Paint paints[];
    private Bitmap bmps[];
    private final int testSizes[] = {50, 35, 20};
    private final int bmpSizes[] = {250, 200, 150};

    public AROverlayView(Activity activity, int meter) {
        super(activity);
        this.activity = (ARActivity) activity;
        arPoints = new ArrayList<>();
        arTouchPoints = new ArrayList<>();
        this.meter = meter;
        createPaintsAndBmps();
    }

    private void createPaintsAndBmps() {
        paints = new Paint[3]; // 30m 이내, 100m 이내, 그 외
        for (int i = 0; i < 3; ++i) {
            Paint.FontMetrics fm = new Paint.FontMetrics();
            paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paints[i].setStyle(Paint.Style.FILL);
            paints[i].setColor(Color.BLACK);
            paints[i].setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            paints[i].setTextSize(testSizes[i]);
            paints[i].getFontMetrics(fm);
        }
        bmps = new Bitmap[3];
        for(int i = 0 ; i < 3 ; ++i) {
            bmps[i] = scaleDown(BitmapFactory.decodeResource(getResources(), R.drawable.ar_rest), bmpSizes[i], true); // 이미지 축소시키기
        }
    }

    public void touch(float x, float y) {
        for (int i = 0; i < arTouchPoints.size(); ++i) {
            ARTouchPoint arTouchPoint = arTouchPoints.get(i);
            if (arTouchPoint.isInTouchPoint(x, y)) {
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

        int margin = 10;
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

                Bitmap bmp = null;
                Paint paint = null;
                if(arPoint.getLocation().distanceTo(currentLocation) <= 30) {
                    bmp = bmps[0];
                    paint = paints[0];
                } else if(arPoint.getLocation().distanceTo(currentLocation) <= 100) {
                    bmp = bmps[1];
                    paint = paints[1];
                } else {
                    bmp = bmps[2];
                    paint = paints[2];
                }

                canvas.drawBitmap(bmp, x - bmp.getWidth() / 2, y - bmp.getHeight() / 2, null); // 식당 아이콘과
                String name = arPoint.getName();
                int bmpHeight = bmp.getHeight() / 4 * 3;

                Paint.FontMetrics fm = paint.getFontMetrics();
                paint.setColor(Color.BLACK);
                canvas.drawRect(x - margin - paint.measureText(name) / 2, y + fm.top - margin + bmpHeight, x + paint.measureText(name) / 2 + margin, y + fm.bottom + margin + bmpHeight, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(name, x - paint.measureText(name) / 2, y + bmpHeight, paint); // 식당 이름을 그린다
                arTouchPoints.add(new ARTouchPoint(arPoint.getID(), x - bmp.getWidth() / 2, y + bmp.getHeight() / 2)); // 터치 포인트 추가
            }
        }
    }
}
