package com.baesullin.pro.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "facInfoList")
public class PublicApiCategoryForm {
    private String resultCode;
    private String resultMessage;
    private List<ServList> servList;

    @NoArgsConstructor
    @Getter @Setter
    @XmlRootElement(name = "servList")
    public static class ServList{
        private String evalInfo;
        private String faclNm;
        private String wfcltId;
        public boolean validation(){
            return this.evalInfo != null;
        }
    }
}
