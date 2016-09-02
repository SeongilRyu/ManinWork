package com.nwsoft.maninwork.beans;

import com.nwsoft.maninwork.backend.transactionApi.model.Transaction;

import java.io.Serializable;

/**
 * Created by Seongil on 2016-04-23.
 */
public class Payrollx implements Serializable {
    public long pid;
    public String gmail;
    public String lname;
    public String cname;
    public int family;
    public int child;
    public String lmobile;
    public String lemail;
    public long payhour;
    public long payday;
    public long paytransit;         public long paytransport;
    public String pym;
    public long workstandards;      public long d_stds;
    public long work_jcs;           public long d_jcs;
    public long work_ygs;           public long d_ygs;
    public long d_total;
    public long d_transits;         public long m_transits;
    public long d_transports;       public long m_transports;
    public long pay_base;
    public long bonusrate;          public long pay_bonus;
    public long workovertimes;      public long m_overs;
    public long workspecials;       public long m_specials;
    public long workspecialovers;   public long m_spovers;
    public long worknights;         public long m_nights;
    public long worklateearlys;     public long m_lateas;
    public long pay_total;
    public long t_income;           public long t_income10;
    public long t_pension;
    public long t_health;           public long t_health10;
    public long t_employ;
    public long t_hurt;
    public long t_prepay;
    public long pay_real;   //net pay
    //


}
