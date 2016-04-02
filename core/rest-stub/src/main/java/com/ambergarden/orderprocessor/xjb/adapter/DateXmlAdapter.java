package com.ambergarden.orderprocessor.xjb.adapter;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XSD mapping adapter which helps to map from String to Date
 * In jaxb, all xsd:date will be mapped to GregorianCalendar by default.
 * It contains more information than Date. But it is not widely supported,
 * for example, GWT does not support it at all.
 */
public class DateXmlAdapter extends XmlAdapter<String, Date> {
   @Override
   public String marshal(Date date) throws Exception {
      final GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      return DatatypeConverter.printDate(calendar);
   }

   @Override
   public Date unmarshal(String dateTime) throws Exception {
      return DatatypeConverter.parseDate(dateTime).getTime();
   }
}