package com.hyperlofy.backend.zone.service;

import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the distance between two points using the Haversine formula.
     *
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.pow(Math.sin(dLon / 2), 2) *
                   Math.cos(radLat1) *
                   Math.cos(radLat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Checks if a point lies within the radius of a center point.
     *
     * @param lat Latitude of checking point
     * @param lon Longitude of checking point
     * @param centerLat Center Latitude
     * @param centerLon Center Longitude
     * @param radiusKm Radius boundary
     * @return true if point is within the range, false otherwise
     */
    public boolean isWithinRadius(double lat, double lon, double centerLat, double centerLon, double radiusKm) {
        return calculateDistanceKm(lat, lon, centerLat, centerLon) <= radiusKm;
    }
}
