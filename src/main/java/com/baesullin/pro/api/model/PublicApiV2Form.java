package com.baesullin.pro.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "facInfoList")
public class PublicApiV2Form {
    private int totalCount;
    private int resultCode;
    private String resultMessage;

    private ServList[] servList;


    @NoArgsConstructor
    @Getter
    @Setter
    @XmlRootElement(name = "servList")
    public static class ServList {
        private String estbDate;
        private String faclInfId;
        private String faclLat; // 위도
        private String faclLng; // 경도
        private String faclNm; // 업장 이름
        private String faclRprnNm; // 담당자명
        private String faclTyCd; // 시설 코드
        private String lcMnad; // 주소
        private String salStaDivCd;
        private String salStaNm;
        private String wfcltDivCd;
        private String wfcltId; // 배리어 프리 시설 고유 번호

        public boolean validateServList() {
            return this.faclLat != null && this.faclLng != null
                    && this.wfcltId != null && this.faclNm != null;
        }
    }
}
