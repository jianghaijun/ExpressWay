package com.zj.expressway.listener;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

import com.zj.expressway.utils.ConstantsUtil;

/**
 * 类描述：实现LocationListener的子类，同时实现自己的接口调用
 * Create dell By 2018/4/16 10:12
 */
public class GPSLocation implements LocationListener {
    private GPSLocationListener mGpsLocationListener;

    public GPSLocation(GPSLocationListener gpsLocationListener) {
        this.mGpsLocationListener = gpsLocationListener;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mGpsLocationListener.UpdateLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        mGpsLocationListener.UpdateStatus(provider, status, extras);
        switch (status) {
            case LocationProvider.AVAILABLE:
                mGpsLocationListener.UpdateGPSProviderStatus(ConstantsUtil.GPS_AVAILABLE);
                break;
            case LocationProvider.OUT_OF_SERVICE:
                mGpsLocationListener.UpdateGPSProviderStatus(ConstantsUtil.GPS_OUT_OF_SERVICE);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                mGpsLocationListener.UpdateGPSProviderStatus(ConstantsUtil.GPS_TEMPORARILY_UNAVAILABLE);
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        mGpsLocationListener.UpdateGPSProviderStatus(ConstantsUtil.GPS_ENABLED);
    }

    @Override
    public void onProviderDisabled(String provider) {
        mGpsLocationListener.UpdateGPSProviderStatus(ConstantsUtil.GPS_DISABLED);
    }
}
