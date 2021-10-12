package org.techtown.hanieum;

import org.techtown.hanieum.db.entity.CvInfo;

public class Certificate {
    private int no;
    private String certifi;
    private String certifiCode;

    public Certificate(){}

    public Certificate(CvInfo cv) {
        this.no = cv.info_no;
        this.certifi = cv.info;
        this.certifiCode = cv.info_code;
    }

    public String getCertifi() {
        return certifi;
    }

    public void setCertifi(String certifi) {
        this.certifi = certifi;
    }

    public String getCertifiCode() {
        return certifiCode;
    }

    public void setCertifiCode(String certifiCode) {
        this.certifiCode = certifiCode;
    }
}
