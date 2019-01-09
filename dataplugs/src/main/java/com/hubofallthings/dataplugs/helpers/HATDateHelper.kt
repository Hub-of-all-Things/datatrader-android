package com.hubofallthings.dataplugs.helpers

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Locale



class HATDateHelper{
    private var formatStrings = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss'Z'", "dd/MM/yyyy", "yyyy-MM-dd'T'")

    fun tryParseDate(dateString: String?): String {
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if(dateString.isNullOrEmpty()){
            return ""
        }
        for (formatString in formatStrings) {
            try {
                val d = SimpleDateFormat(formatString,Locale.getDefault()).parse(dateString)
                return output.format(d)
            } catch (e: ParseException) {
            }
        }
        return ""
    }
    fun tryParseDateOutput(dateString: String? , outputFormat : String): String {
        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        if(dateString.isNullOrEmpty()){
            return ""
        }
        for (formatString in formatStrings) {
            try {
                val d = SimpleDateFormat(formatString,Locale.getDefault()).parse(dateString)
                return output.format(d)
            } catch (e: ParseException) {
            }
        }
        return ""
    }
    fun convertIsoToUnix(dateString: String?): Long {
        if(dateString.isNullOrEmpty()){
            return currentDateUnix()
        }
        for (formatString in formatStrings) {
            try {
                val d = SimpleDateFormat(formatString,Locale.getDefault()).parse(dateString)
                return d.time/1000L
            } catch (e: ParseException) {
            }
        }
        return currentDateUnix()
    }
    fun getFeedToday(date : String) : Boolean{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val strDate = inputFormat.parse(date)
        return System.currentTimeMillis() > strDate.getTime()
    }
    fun parseBirthDate(birthDate : String?) : String{
        val input = SimpleDateFormat("yyyy-MM-dd'T'", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if(birthDate.isNullOrEmpty()){
            return ""
        }

        val d = input.parse(birthDate)
        return output.format(d)
    }
    fun getDateFormat(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat,Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
    fun getFeedDateHeader (date : Number ) : String {
        val dateMil = date.toLong()*1000L
        val dateF = getDateFormat(dateMil,"EEE d MMM yyyy")
        return dateF.toUpperCase()
    }
    fun birthDateToUpload(birthDate : String?) : String{
        val input = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        if(birthDate.isNullOrEmpty()){
            return ""
        }
        val d = input.parse(birthDate)
        return output.format(d)
    }
    fun currentDateIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'" , Locale.getDefault())
        val now = Date()
        return sdf.format(now)
    }
    //get the current date unix
    fun currentDateUnix(): Long {
        val cal = Calendar.getInstance()
        return cal.timeInMillis / 1000L
    }

}