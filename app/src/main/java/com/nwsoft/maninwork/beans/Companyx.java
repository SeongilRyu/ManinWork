package com.nwsoft.maninwork.beans;

import java.io.Serializable;

/**
 * Created by Seongil on 2016-04-23.
 */
public class Companyx implements Serializable {
    public long cid;
    public String gmail;
    public String cname;
    public String crep;
    public String crepmobile;
    public String cemail;
    public long cpayperhour;
    public long cpaydays;
    public long cpaytransport;
    public long cpaytransition;
    public long cbonusrate;
    public String caddress;
    public String cdata;
    public String cregdate;

    public void fill(com.nwsoft.maninwork.backend.companyApi.model.Company o) {
        cid = o.getCid();
        gmail=o.getGmail();
        cname=o.getCname();
        crep=o.getCrep();
        crepmobile=o.getCrepmobile();
        cemail=o.getCemail();
        cpayperhour=o.getCpayperhour();
        cpaydays=o.getCpaydays();
        cpaytransport=o.getCpaytransport();
        cpaytransition=o.getCpaytransition();
        cbonusrate=o.getCbonusrate();
        caddress=o.getCaddress();
        cdata=o.getCdata();
        cregdate=o.getCregdate();
    }
    @Override
    public String toString() {
        return "Companyx{" +
                "cid=" + cid +
                ", gmail='" + gmail + '\'' +
                ", cname='" + cname + '\'' +
                ", crep='" + crep + '\'' +
                ", crepmobile='" + crepmobile + '\'' +
                ", cemail='" + cemail + '\'' +
                ", cpayperhour=" + cpayperhour +
                ", cpaydays=" + cpaydays +
                ", cpaytransport=" + cpaytransport +
                ", cpaytransition=" + cpaytransition +
                ", cbonusrate=" + cbonusrate +
                ", caddress='" + caddress + '\'' +
                ", cdata='" + cdata + '\'' +
                ", cregdate='" + cregdate + '\'' +
                '}';
    }
}
