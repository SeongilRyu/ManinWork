package com.nwsoft.maninwork.backend;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by admin on 2016-06-22.
 */
@Entity
public class Transaction {
    @Id
    Long tid;
    @Index String gmail;
    @Index String lname;
    @Index String cname;
    String tym;
    String tdate;
    String weekday;
    String starttime;
    String endtime;
    Long workstandard;
    Long work_jc;
    Long work_yg;
    Long workovertime;
    Long workspecial;
    Long workspecialover;
    Long worknight;
    Long worklateearly;
    String tdesc;
    String tregdate;

    public Transaction() {
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getTym() {
        return tym;
    }

    public void setTym(String tym) {
        this.tym = tym;
    }

    public String getTdate() {
        return tdate;
    }

    public void setTdate(String tdate) {
        this.tdate = tdate;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Long getWorkstandard() {
        return workstandard;
    }

    public void setWorkstandard(Long workstandard) {
        this.workstandard = workstandard;
    }

    public Long getWork_jc() {
        return work_jc;
    }

    public void setWork_jc(Long work_jc) {
        this.work_jc = work_jc;
    }

    public Long getWork_yg() {
        return work_yg;
    }

    public void setWork_yg(Long work_yg) {
        this.work_yg = work_yg;
    }

    public Long getWorkovertime() {
        return workovertime;
    }

    public void setWorkovertime(Long workovertime) {
        this.workovertime = workovertime;
    }

    public Long getWorkspecial() {
        return workspecial;
    }

    public void setWorkspecial(Long workspecial) {
        this.workspecial = workspecial;
    }

    public Long getWorkspecialover() {
        return workspecialover;
    }

    public void setWorkspecialover(Long workspecialover) {
        this.workspecialover = workspecialover;
    }

    public Long getWorknight() {
        return worknight;
    }

    public void setWorknight(Long worknight) {
        this.worknight = worknight;
    }

    public Long getWorklateearly() {
        return worklateearly;
    }

    public void setWorklateearly(Long worklateearly) {
        this.worklateearly = worklateearly;
    }

    public String getTdesc() {
        return tdesc;
    }

    public void setTdesc(String tdesc) {
        this.tdesc = tdesc;
    }

    public String getTregdate() {
        return tregdate;
    }

    public void setTregdate(String tregdate) {
        this.tregdate = tregdate;
    }
}
