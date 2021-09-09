package org.techtown.hanieum;

public class Certificate {
    private String certifi;
    private String certifiCode;

    public Certificate(String certifi, String certifiCode) {
        this.certifi = certifi;
        this.certifiCode = certifiCode;
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
