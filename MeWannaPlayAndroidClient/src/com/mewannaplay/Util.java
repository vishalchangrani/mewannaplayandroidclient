package com.mewannaplay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

	public static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String getUTCTimeForHourMinute(int hour, int minutes)
	{
		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR_OF_DAY,hour);
		c.set(Calendar.MINUTE, minutes);
		c.set(Calendar.SECOND, 0);
		int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
		 
		int dstOffset = c.get(java.util.Calendar.DST_OFFSET);
		 
		c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		 
		Date finalUTCDate = c.getTime();
		 
		String finalUTCDateAsString = fmt.format(finalUTCDate);
		 
		return finalUTCDateAsString;
	}

	public static final String getLocalTimeFromUTC(String utcTime)
	{
		try
		{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone utcZone = TimeZone.getTimeZone("UTC");
		simpleDateFormat.setTimeZone(utcZone);
		Date myDate = simpleDateFormat.parse(utcTime);
		simpleDateFormat.setTimeZone(TimeZone.getDefault());
		String formattedDate = simpleDateFormat.format(myDate);
		return formattedDate;
		}
		catch(Exception e)
		{
			return "";
		}

	}
	
}
