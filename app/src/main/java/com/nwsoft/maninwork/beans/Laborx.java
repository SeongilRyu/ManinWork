package com.nwsoft.maninwork.beans;

import com.nwsoft.maninwork.backend.laborApi.model.Labor;

import java.io.Serializable;

/**
 * Created by Seongil on 2016-04-23.
 */
public class Laborx implements Serializable {
    public long lid;
    public String gmail;
    public String lname;
    public String cname;
    public String lmobile;
    public long lfamily;
    public long lchild;
    public String lemail;
    public long lbasepay;
    public long lworkday;
    public long lpay;
    public String lregdate;

    public void fillme(com.nwsoft.maninwork.backend.laborApi.model.Labor o) {
        lid = o.getLid();
        gmail=o.getGmail();
        lname=o.getLname();
        cname=o.getCname();
        lmobile=o.getLmobile();
        lfamily=o.getLfamily();
        lchild=o.getLchild();
        lemail=o.getLemail();
        lbasepay=o.getLbasepay();
        lworkday=o.getLworkday();
        lpay=o.getLpay();
        lregdate=o.getLregdate();
    }
    public Labor fill2Labor() {
        Labor o = new Labor();
        o.setLid(this.lid);
        o.setGmail(this.gmail);
        o.setLname(this.lname);
        o.setCname(this.cname);
        o.setLmobile(this.lmobile);
        o.setLfamily(this.lfamily);
        o.setLchild(this.lchild);
        o.setLemail(this.lemail);
        o.setLbasepay(this.lbasepay);
        o.setLworkday(this.lworkday);
        o.setLpay(this.lpay);
        o.setLregdate(this.lregdate);
        return o;
    }
    @Override
    public String toString() {
        return "Laborx{" +
                "lid=" + lid +
                ", gmail='" + gmail + '\'' +
                ", lname='" + lname + '\'' +
                ", cname='" + cname + '\'' +
                ", lmobile='" + lmobile + '\'' +
                ", lfamily=" + lfamily +
                ", lchild=" + lchild +
                ", lemail='" + lemail + '\'' +
                ", lbasepay=" + lbasepay +
                ", lworkday=" + lworkday +
                ", lpay=" + lpay +
                ", lregdate='" + lregdate + '\'' +
                '}';
    }
}
