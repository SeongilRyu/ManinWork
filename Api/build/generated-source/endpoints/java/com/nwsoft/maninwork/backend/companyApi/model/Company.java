/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-07-08 17:28:43 UTC)
 * on 2016-09-01 at 12:08:44 UTC 
 * Modify at your own risk.
 */

package com.nwsoft.maninwork.backend.companyApi.model;

/**
 * Model definition for Company.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the companyApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Company extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String caddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cbonusrate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cdata;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cemail;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cid;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cname;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cpaydays;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cpayperhour;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cpaytransition;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long cpaytransport;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String cregdate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String crep;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String crepmobile;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String gmail;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCaddress() {
    return caddress;
  }

  /**
   * @param caddress caddress or {@code null} for none
   */
  public Company setCaddress(java.lang.String caddress) {
    this.caddress = caddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCbonusrate() {
    return cbonusrate;
  }

  /**
   * @param cbonusrate cbonusrate or {@code null} for none
   */
  public Company setCbonusrate(java.lang.Long cbonusrate) {
    this.cbonusrate = cbonusrate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCdata() {
    return cdata;
  }

  /**
   * @param cdata cdata or {@code null} for none
   */
  public Company setCdata(java.lang.String cdata) {
    this.cdata = cdata;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCemail() {
    return cemail;
  }

  /**
   * @param cemail cemail or {@code null} for none
   */
  public Company setCemail(java.lang.String cemail) {
    this.cemail = cemail;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCid() {
    return cid;
  }

  /**
   * @param cid cid or {@code null} for none
   */
  public Company setCid(java.lang.Long cid) {
    this.cid = cid;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCname() {
    return cname;
  }

  /**
   * @param cname cname or {@code null} for none
   */
  public Company setCname(java.lang.String cname) {
    this.cname = cname;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCpaydays() {
    return cpaydays;
  }

  /**
   * @param cpaydays cpaydays or {@code null} for none
   */
  public Company setCpaydays(java.lang.Long cpaydays) {
    this.cpaydays = cpaydays;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCpayperhour() {
    return cpayperhour;
  }

  /**
   * @param cpayperhour cpayperhour or {@code null} for none
   */
  public Company setCpayperhour(java.lang.Long cpayperhour) {
    this.cpayperhour = cpayperhour;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCpaytransition() {
    return cpaytransition;
  }

  /**
   * @param cpaytransition cpaytransition or {@code null} for none
   */
  public Company setCpaytransition(java.lang.Long cpaytransition) {
    this.cpaytransition = cpaytransition;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCpaytransport() {
    return cpaytransport;
  }

  /**
   * @param cpaytransport cpaytransport or {@code null} for none
   */
  public Company setCpaytransport(java.lang.Long cpaytransport) {
    this.cpaytransport = cpaytransport;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCregdate() {
    return cregdate;
  }

  /**
   * @param cregdate cregdate or {@code null} for none
   */
  public Company setCregdate(java.lang.String cregdate) {
    this.cregdate = cregdate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCrep() {
    return crep;
  }

  /**
   * @param crep crep or {@code null} for none
   */
  public Company setCrep(java.lang.String crep) {
    this.crep = crep;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCrepmobile() {
    return crepmobile;
  }

  /**
   * @param crepmobile crepmobile or {@code null} for none
   */
  public Company setCrepmobile(java.lang.String crepmobile) {
    this.crepmobile = crepmobile;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGmail() {
    return gmail;
  }

  /**
   * @param gmail gmail or {@code null} for none
   */
  public Company setGmail(java.lang.String gmail) {
    this.gmail = gmail;
    return this;
  }

  @Override
  public Company set(String fieldName, Object value) {
    return (Company) super.set(fieldName, value);
  }

  @Override
  public Company clone() {
    return (Company) super.clone();
  }

}
