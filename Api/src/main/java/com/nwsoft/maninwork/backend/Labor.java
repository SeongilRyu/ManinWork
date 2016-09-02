package com.nwsoft.maninwork.backend;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;

/**
 * Created by admin on 2016-06-22.
 */
@Entity
public class Labor {
    @Id
    Long lid;
    @Index String gmail;
    @Index String lname;
    @Index String cname;
    String lmobile;
    Long lfamily;
    Long lchild;
    String lemail;
    Long lbasepay;
    Long lworkday;
    Long lpay;
    String lregdate;

    public Labor() {
    }

    public Long getLid() {
        return lid;
    }

    public void setLid(Long lid) {
        this.lid = lid;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getLmobile() {
        return lmobile;
    }

    public void setLmobile(String lmobile) {
        this.lmobile = lmobile;
    }

    public Long getLfamily() {
        return lfamily;
    }

    public void setLfamily(Long lfamily) {
        this.lfamily = lfamily;
    }

    public Long getLchild() {
        return lchild;
    }

    public void setLchild(Long lchild) {
        this.lchild = lchild;
    }

    public String getLemail() {
        return lemail;
    }

    public void setLemail(String lemail) {
        this.lemail = lemail;
    }

    public Long getLbasepay() {
        return lbasepay;
    }

    public void setLbasepay(Long lbasepay) {
        this.lbasepay = lbasepay;
    }

    public Long getLworkday() {
        return lworkday;
    }

    public void setLworkday(Long lworkday) {
        this.lworkday = lworkday;
    }

    public Long getLpay() {
        return lpay;
    }

    public void setLpay(Long lpay) {
        this.lpay = lpay;
    }

    public String getLregdate() {
        return lregdate;
    }

    public void setLregdate(String lregdate) {
        this.lregdate = lregdate;
    }
}
