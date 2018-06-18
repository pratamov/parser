package com.ef.configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.beust.jcommander.IStringConverter;

public class DateConverter implements IStringConverter<Date>{
	
	public static final String ARGUMENT_DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss";
	
	@Override
	public Date convert(String value) {
		
		try {
			Date date = new SimpleDateFormat(ARGUMENT_DATE_FORMAT).parse(value);
			return date;
		} catch (ParseException e) {
			return new Date();
		}
		
	}
	
	public static Date convert(String value, String fomat) {
		
		try {
			Date date = new SimpleDateFormat(fomat).parse(value);
			return date;
		} catch (ParseException e) {
			return null;
		}
		
	}
	
	public static String toString(Date date, String format) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);

	}

}
