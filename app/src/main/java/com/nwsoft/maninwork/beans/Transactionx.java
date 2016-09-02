package com.nwsoft.maninwork.beans;

import com.nwsoft.maninwork.backend.transactionApi.TransactionApi;
import com.nwsoft.maninwork.backend.transactionApi.model.*;

import java.io.Serializable;

/**
 * Created by Seongil on 2016-04-23.
 */
public class Transactionx implements Serializable {
    public long tid;
    public String gmail;
    public String lname;
    public String cname;
    public String tym;
    public String tdate;
    public String weekday;
    public String starttime;
    public String endtime;
    public long workstandard;
    public long work_jc;
    public long work_yg;
    public long workovertime;
    public long workspecial;
    public long workspecialover;
    public long worknight;
    public long worklateearly;
    public String tdesc;
    public String tregdate;

    public void fillme(com.nwsoft.maninwork.backend.transactionApi.model.Transaction t) {
        tid = t.getTid();
        gmail=t.getGmail();
        lname=t.getLname();
        cname=t.getCname();
        tym=t.getTym();
        tdate=t.getTdate();
        weekday=t.getWeekday();
        starttime=t.getStarttime();
        endtime=t.getEndtime();
        workstandard=t.getWorkstandard();
        work_jc=t.getWorkJc();
        work_yg=t.getWorkYg();
        workovertime=t.getWorkovertime();
        workspecial=t.getWorkspecial();
        workspecialover=t.getWorkspecialover();
        worknight=t.getWorknight();
        worklateearly=t.getWorklateearly();
        tdesc=t.getTdesc();
        tregdate=t.getTregdate();
    }
    @Override
    public String toString() {
        return "Transactionx{" +
                "tid=" + tid +
                ", gmail='" + gmail + '\'' +
                ", lname='" + lname + '\'' +
                ", cname='" + cname + '\'' +
                ", tym='" + tym + '\'' +
                ", tdate='" + tdate + '\'' +
                ", weekday='" + weekday + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", workstandard=" + workstandard +
                ", work_jc=" + work_jc +
                ", work_yg=" + work_yg +
                ", workovertime=" + workovertime +
                ", workspecial=" + workspecial +
                ", workspecialover=" + workspecialover +
                ", worknight=" + worknight +
                ", worklateearly=" + worklateearly +
                ", tdesc='" + tdesc + '\'' +
                ", tregdate='" + tregdate + '\'' +
                '}';
    }
}
