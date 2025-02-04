package com.attendance.location.service;

import com.attendance.location.model.Location;
import com.attendance.pkl.entity.PKLAssignment;
import org.springframework.stereotype.Service;

@Service
public class LocationValidationService {

    private static final double EARTH_RADIUS = 6371000; // meters

    public boolean isLocationValid(Location currentLocation, PKLAssignment assignment) {
        if (assignment == null || currentLocation == null) {
            return false;
        }

        double distance = calculateDistance(
            currentLocation.getLatitude(),
            currentLocation.getLongitude(),
            assignment.getCompanyLatitude(),
            assignment.getCompanyLongitude()
        );

        return distance <= assignment.getAllowedRadius();
    }

    public boolean isLocationValid(Location currentLocation, double targetLat, double targetLng, double allowedRadius) {
        if (currentLocation == null) {
            return false;
        }

        double distance = calculateDistance(
            currentLocation.getLatitude(),
            currentLocation.getLongitude(),
            targetLat,
            targetLng
        );

        return distance <= allowedRadius;
    }

    /**
     * Calculate distance between two points using the Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Calculate if a point is within a polygon (for complex geofencing)
     */
    public boolean isPointInPolygon(Location point, Location[] polygon) {
        if (polygon.length < 3) {
            return false;
        }

        boolean inside = false;
        int j = polygon.length - 1;

        for (int i = 0; i < polygon.length; i++) {
            if ((polygon[i].getLongitude() < point.getLongitude() && polygon[j].getLongitude() >= point.getLongitude()
                    || polygon[j].getLongitude() < point.getLongitude() && polygon[i].getLongitude() >= point.getLongitude())
                    && (polygon[i].getLatitude() <= point.getLatitude()
                    || polygon[j].getLatitude() <= point.getLatitude())) {
                if (polygon[i].getLatitude() + (point.getLongitude() - polygon[i].getLongitude())
                        / (polygon[j].getLongitude() - polygon[i].getLongitude())
                        * (polygon[j].getLatitude() - polygon[i].getLatitude()) < point.getLatitude()) {
                    inside = !inside;
                }
            }
            j = i;
        }

        return inside;
    }
}
