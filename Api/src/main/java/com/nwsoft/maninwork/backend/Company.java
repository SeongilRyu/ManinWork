package com.nwsoft.maninwork.backend;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by admin on 2016-06-22.
 */
@Entity
public class Company {
    @Id
    Long cid;
    @Index String gmail;
    @Index String cname;
    String crep;
    String crepmobile;
    String cemail;
    Long cpayperhour;
    Long cpaydays;
    Long cpaytransport;
    Long cpaytransition;
    Long cbonusrate;
    String caddress;
    String cdata;
    String cregdate;

    public Company() {
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getCrep() {
        return crep;
    }

    public void setCrep(String crep) {
        this.crep = crep;
    }

    public String getCrepmobile() {
        return crepmobile;
    }

    public void setCrepmobile(String crepmobile) {
        this.crepmobile = crepmobile;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public Long getCpayperhour() {
        return cpayperhour;
    }

    public void setCpayperhour(Long cpayperhour) {
        this.cpayperhour = cpayperhour;
    }

    public Long getCpaydays() {
        return cpaydays;
    }

    public void setCpaydays(Long cpaydays) {
        this.cpaydays = cpaydays;
    }

    public Long getCpaytransport() {
        return cpaytransport;
    }

    public void setCpaytransport(Long cpaytransport) {
        this.cpaytransport = cpaytransport;
    }

    public Long getCpaytransition() {
        return cpaytransition;
    }

    public void setCpaytransition(Long cpaytransition) {
        this.cpaytransition = cpaytransition;
    }

    public Long getCbonusrate() {
        return cbonusrate;
    }

    public void setCbonusrate(Long cbonusrate) {
        this.cbonusrate = cbonusrate;
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    public String getCdata() {
        return cdata;
    }

    public void setCdata(String cdata) {
        this.cdata = cdata;
    }

    public String getCregdate() {
        return cregdate;
    }

    public void setCregdate(String cregdate) {
        this.cregdate = cregdate;
    }
}
