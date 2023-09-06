package com.baesullin.pro.api.service;

import com.baesullin.pro.api.dto.LocationInfoDto;
import com.baesullin.pro.api.dto.LocationPartDto;

import java.util.List;

public interface LocationService {

    LocationPartDto.LatLong convertAddressToGeo(String address);

    LocationInfoDto.LocationResponse convertGeoAndStoreNameToKeyword(String lat, String lng, String storeName);

    List<LocationInfoDto.LocationResponse> convertGeoAndAddressToKeyword(String lat, String lng, String address);

    LocationPartDto.Address convertGeoToAddress(String lat, String lng);
}
