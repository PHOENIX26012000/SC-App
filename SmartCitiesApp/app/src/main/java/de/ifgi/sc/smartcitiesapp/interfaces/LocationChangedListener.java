package de.ifgi.sc.smartcitiesapp.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Maurin on 09.07.2016.
 */
public interface LocationChangedListener {

    public void onLocationChanged(LatLng newLocation);

}
