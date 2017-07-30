package com.grameenphone.mars.utility;



public class BanglaConvert {

    public static String convertBangla(String eng) {

        if(eng.contains("moments ago")) eng = eng.replace("moments ago", "এখন");
        if(eng.contains("min ago")) eng = eng.replace("min ago", "মিনিট আগে");
        if(eng.contains("hrs ago")) eng = eng.replace("hrs ago", "ঘন্টা আগে");
        if(eng.contains("1")) eng = eng.replace("1", "১");
        if(eng.contains("2")) eng = eng.replace("2", "২");
        if(eng.contains("3")) eng = eng.replace("3", "৩");
        if(eng.contains("4")) eng = eng.replace("4", "৪");
        if(eng.contains("5")) eng = eng.replace("5", "৫");
        if(eng.contains("6")) eng = eng.replace("6", "৬");
        if(eng.contains("7")) eng = eng.replace("7", "৭");
        if(eng.contains("8")) eng = eng.replace("8", "৮");
        if(eng.contains("9")) eng = eng.replace("9", "৯");
        if(eng.contains("0")) eng = eng.replace("0", "০");
        if(eng.contains("Jan")) eng = eng.replace("Jan", "জানুয়ারি");
        if(eng.contains("Feb")) eng = eng.replace("Feb", "ফেব্রুয়ারি");
        if(eng.contains("Mar")) eng = eng.replace("Mar", "মার্চ");
        if(eng.contains("Apr")) eng = eng.replace("Apr", "এপ্রিল");
        if(eng.contains("May")) eng = eng.replace("May", "মে");
        if(eng.contains("Jun")) eng = eng.replace("Jun", "জুন");
        if(eng.contains("Jul")) eng = eng.replace("Jul", "জুলাই");
        if(eng.contains("Aug")) eng = eng.replace("Aug", "অগাস্ট");
        if(eng.contains("Sep")) eng = eng.replace("Sep", "সেপ্টেম্বর");
        if(eng.contains("Oct")) eng = eng.replace("Oct", "অক্টোবর");
        if(eng.contains("Nov")) eng = eng.replace("Nov", "নভেম্বর");
        if(eng.contains("Dec")) eng = eng.replace("Dec", "ডিসেম্বর");
        return eng;
    }

    public static String getBanglaTime(String h){
        if (ifBhor(h))
            return "ভোর";
        if (ifShokal(h))
            return "সকাল";
        if (ifDupur(h))
            return "দুপুর";
        if (ifBikal(h))
            return "বিকাল";
        if (ifShondha(h))
            return "সন্ধ্যা";
        if (ifRaat(h))
            return "রাত";
        else return "";
    }

    public static boolean ifBhor(String hour){
        if (hour.equals("04") || hour.equals("05") || hour.equals("06"))
            return true;
        return false;
    }

    public static boolean ifShokal(String hour){
        if (hour.equals("07") || hour.equals("08") || hour.equals("09") || hour.equals("10") || hour.equals("11"))
            return true;
        return false;
    }

    public static boolean ifDupur(String hour){
        if (hour.equals("12") || hour.equals("13") || hour.equals("14") || hour.equals("15"))
            return true;
        return false;
    }

    public static boolean ifBikal(String hour){
        if (hour.equals("16") || hour.equals("17"))
            return true;
        return false;
    }

    public static boolean ifShondha(String hour){
        if (hour.equals("18") || hour.equals("19"))
            return true;
        return false;
    }

    public static boolean ifRaat(String hour){
        if (hour.equals("20") || hour.equals("21") || hour.equals("22") || hour.equals("23") || hour.equals("00") ||
                hour.equals("01") || hour.equals("02") || hour.equals("03"))
            return true;
        return false;
    }
}
