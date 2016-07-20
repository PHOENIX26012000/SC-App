package de.ifgi.sc.smartcitiesapp.interfaces;

import com.google.android.gms.maps.model.LatLng;


public interface LocationChangedListener {

    /**
     * To be implemented by the main activity to listen to location changed events
     *
     * @param newLocation
     */
    public void onLocationChanged(LatLng newLocation);

}
