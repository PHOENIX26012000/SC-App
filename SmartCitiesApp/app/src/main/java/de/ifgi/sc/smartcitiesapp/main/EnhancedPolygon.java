package de.ifgi.sc.smartcitiesapp.main;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;


public class EnhancedPolygon {

    private int colorRed;
    private int colorGreen;
    private int colorBlue;
    private ArrayList<LatLng> points;
    private Polygon polygon;
    private String name;

    public EnhancedPolygon(ArrayList<LatLng> coordinates, int[] color, String name) {
        points = new ArrayList<LatLng>();
        for (LatLng lt : coordinates) {
            points.add(lt);
        }
        this.colorRed = color[0];
        this.colorGreen = color[1];
        this.colorBlue = color[2];
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public PolygonOptions getPolygon() {
        return new PolygonOptions()
                .zIndex(100)
                .fillColor(android.graphics.Color.argb(100, this.colorRed, this.colorGreen, this.colorBlue))
                .strokeWidth(1)
                .strokeColor(android.graphics.Color.argb(100, this.colorRed, this.colorGreen, this.colorBlue))
                .addAll(this.points);
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygonRef() {
        return this.polygon;
    }

    public void setDefaultColor() {
        this.polygon.setFillColor(Color.argb(100, this.colorRed, this.colorGreen, this.colorBlue));
    }

    public ArrayList<LatLng> getPoints() {
        return this.points;
    }

}
