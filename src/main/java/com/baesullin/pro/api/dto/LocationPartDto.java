package com.baesullin.pro.api.dto;

import com.baesullin.pro.api.model.LocationAddressSearchForm;
import com.baesullin.pro.api.model.LocationKeywordSearchForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@AllArgsConstructor
@Getter @Setter
@Builder
public class LocationPartDto {

    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class LatLong{
        @Builder.Default
        private boolean status = false;
        private String latitude;
        private String longitude;

        public boolean validate() {
            return this.latitude != null && this.longitude != null;
        }
        public static LatLong convertPart(LocationKeywordSearchForm locationKeywordSearchForm){
            LatLong locLl = LatLong.builder().build();
            if (locationKeywordSearchForm == null) { // 비어 있을 때 status-false 저장
                return locLl;
            }
            LocationKeywordSearchForm.Documents latLngDoc
                    = Arrays.stream(locationKeywordSearchForm.getDocuments()).findAny().orElse(null);
            if (latLngDoc != null) {
                locLl = LatLong.builder()
                        .latitude(latLngDoc.getY())
                        .longitude(latLngDoc.getX())
                        .status(true)
                        .build();
            }
            return locLl;
        }
    }

    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class Address{
        @Builder.Default
        private boolean status = false;
        private String address;
        public static Address formToDto(LocationAddressSearchForm resultRe) {
            Address addressInfoDto = Address.builder().build();
            if (resultRe == null)
                return addressInfoDto;

            LocationAddressSearchForm.TotalAddress address = Arrays.stream(resultRe.getDocuments()).findFirst().orElse(null);
            if (address == null) {
                return addressInfoDto;
            } else {
                return Address.builder()
                        .address(address.getAddress().getAddress_name())
                        .status(true)
                        .build();
            }
        }
    }
}
