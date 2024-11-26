package com.github.accountmanagementproject.service.runJoinPost;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



public class GeoUtil {

    private GeoUtil() {
        // private 생성자로 인스턴스화 방지
    }

    // 거리계산 Haversine formula
    // double lat1, double lon1 : 시작위치,   double lat2, double lon2 : 종료위치
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; // 지구 반경 (킬로미터)
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
