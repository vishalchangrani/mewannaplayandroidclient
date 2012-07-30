package com.mewannaplay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;

public class EmailValidator
{
           private Pattern pattern;
           private Matcher matcher;
           private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
           public Boolean validate(String email)
           {
                         pattern = Pattern.compile(EMAIL_PATTERN);
                         matcher = pattern.matcher(email);
                         Boolean matching= matcher.matches();
                          return matching;  //True for pattern matching else, false returned
             }
 }