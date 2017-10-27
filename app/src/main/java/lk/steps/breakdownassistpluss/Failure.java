package lk.steps.breakdownassistpluss;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JagathPrasanga on 2017-06-23.
 */

public class Failure {
    /*public static String[] Type = {
            "Please select",
            "Medium voltage",
            "Low voltage",
            "Service main"
    };

    public static String[] Cause1 = {
            "Please select",
            "Fuse blown",
            "Low voltage issue",
            "Neutral leakage",
            "Low voltage",
            "Service main"
    };

    public static String[] Cause2 = {
            "Please select",
            "DDLO fuse blown",
            "Overhead line"
    };

    public static String[] Cause3 = {
            "Please select",
            "House",
            "Service pole"
    };
    
    public static String[] Description1 = {
            "Please select",
            "Accident due to vehicle",
            "Bad weather condition",
            "Broken conductor",
            "Broken Pole",
            "Burnt tail wire",
            "Due to bird and animal",
            "Loose span and entanglement",
            "Vegetation",
    };

    public static String[] Description2 = {
            "Please select",
            "HT fuse blown",
            "Loose span and entanglement",
            "Vegetation",
    };

    public static String[] Description3 = {
            "Please select",
            "cracked insulation",
            "Due to service Wire shorting",
            "Loose span and entanglement",
    };


    public static String[] Description4 = {
            "Please select",
            "Accident due to vehicle",
            "Bad Weather",
            "Branches coming from distance",
            "Cracked insulator",
            "DDLO carrier damage",
            "Due to lines over loading",
            "Other fault",
            "Vegetation",
    };

    public static String[] Description5 = {
            "Please select",
            "Accident due to vehicle",
            "Bad weather",
            "Broken/damage HT conductor",
            "Burnt HT conductor",
            "Insulator damaged",
            "Jumper ",
            "Loose connection at jumper point",
            "Other fault",
            "Spark at the conductor due to entanglement",
            "Tree branches coming from distance",
            "Vegetation",
    };

    public static String[] Description6 = {
            "Please select",
            "AConsumer fault",
            "MCB tripped",
            "Meter fault",
            "Other Fault"
    };

    public static String[] Description7 = {
            "Please select",
            "Connection point at pole",
            "Other fault",
            "Pole damage",
            "Wire broken",
    };*/


    public static String[] VisitedComments = {
            "Please select",
            "No required material",
            "No required resource",
            "Time consuming",
    };

    public static String[] AttendingComments = {
            "Please select",
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
    };

    public static String[] DoneComments = {
            "Please select",
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
    };
    public static String[] RejectComments = {
            "Please select",
            "Internal fault",
            "No fault",
            "Other",
    };
    public static String[] CompletedComments = {
            "Please select",
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
    };
    public static String[][] ComplainTypeList77 = {
            {"77", "1", "Supply failed at home"},
            {"77", "2", "Supply failed in area"},
            {"77", "3", "Broken service wire"},
            {"77", "4", "Abnormal/ Low Voltage"},
            {"77", "5", "Conductor Burning"},
            {"77", "6", "Flood"},
            {"77", "7", "Flashing Insulators"},
            {"77", "8", "High Voltage"},
            {"77", "9", "Meter Burning"},
            {"77", "10", "Road accident cause pole damage"},
            {"77", "11", "Road accident cause Cable / Conductor damage"},
            {"77", "12", "Tree has fallen on to the line"},
            {"77", "13", "Other"}
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////


    //[AreaId], [FailureTypeId], [FailureTypeName]
    public static String[][] FailureTypeList77 = {
            {"77", "0","-", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "1","-", "Service Connection Failure", "සේවා සැපයුම් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "2","-", "LV Failure", "LV බිඳවැටුම්"},
            {"77", "3","-", "MV Failure", "MV බිඳවැටුම්"}};


    //[AreaId], [FailureNatureId], [FailureNatureName], [ParentFailureTypeId]
    public static String[][] FailureNatureList77 = {
            {"77", "0", "1", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "1", "1", "Service wire breakdowns",  "සේවා රැහැන ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "2", "1", "Aluminum Breakdowns",  "ඇළුමිනියම් රැහැන් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "3", "1", "ABC Breakdowns",  "ABC රැහැන් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "4", "1", "Bimetallic Breakdowns",  "බයිමෙටලික් ආශ්\u200Dරිත බිඳවැටුම්."},
            {"77", "5", "1", "Meter Breakdowns",  "මනුව ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "6", "1", "Cut Out Terminal Breakdowns",  "මනු කටවුට් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "7", "1", "MCCB Failures", "MCCB ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "8", "1", "Other",  "වෙනත්"},

            {"77", "0", "2","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "9", "2","Failures due to Tree / Branch fallen Down",  "ගස් අතු වැටීම නිසා සිදුවන හානි"},
            {"77", "10", "2", "Line Jumper Failures",  "රැහැන් ජම්පර්  ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "11", "2","Breakdowns in Substation",  "තාරාපැවි ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "12", "2","Conductor Breakdowns ",  "කම්බි නඩත්තු නොකිරිම නිසා ඇතිවන බිඳවැටුම්"},
            {"77", "13", "2", "Pole Breakdowns",  "කණු ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "14", "2","Fuse",  "Feeder Fuse ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "15", "2","UG Cable Failures",  "UG කේබල් බිඳවැටුම්"},
            {"77", "16", "2","Other", "වෙනත්"},

            {"77", "0", "3","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "17","3", "11kV",  "11kV"},
            {"77", "18","3", "33kV",  "33kV"}};


    //[AreaId], [FailureCauseId], [FailureCauseName], [ParentFailureNatureId]
    public static String[][] FailureCauseList77 = {
            {"77", "0", "1", "Please select",  "කරුණාකර තෝරන්න"},
            {"77", "1", "1", "Broken Wire / Loose connection due to monkey", "වයරය කැඩීම හෝ බුරුල් වීම ( රිලවුන් නිසා)"},
            {"77", "2", "1", "Burnt Service Wire / Loop Service Breakdowns", "සේවා/ලූප් රැහැන් පිළිස්සී තීබීම"},
            {"77", "3", "1", "Wire damage due to Tree/Branch Fallen Down", "ගස් අතු වැටීම නිසා සිදුවන හානි"},
            {"77", "4", "1", "Wire damage due to human activities", "අලාභ කිරීම් ( මිනිස් ක්\u200Dරියාකාරකම් )"},

            {"77", "0", "2", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "5", "2", "No H Connectors", "H කනෙක්ටර් නොමැති වීම"},
            {"77", "6", "2", "Loose / Burnt Service connection", "සේවා රැහැන් ගැලවී තිබීම හෝ පිළිස්සී තීබීම"},

            {"77", "0", "3", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "7", "3", "No ABC Connectors", "ABC කනෙක්ටර් නොමැති වීම"},
            {"77", "8", "3", "Burnt ABC  Connectors", "ABC කනෙක්ටර් පිළිස්සී තීබීම"},

            {"77", "0", "4", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "9", "4", "Copper / Aluminum Oxidation - From the pole", "කොපර් / ඇළුමිනියම් ඔක්සයිඩ් බැඳීම ( කණුවෙන් )"},
            {"77", "10","4",  "Copper/ Aluminum loose connection", "කොපර් / ඇළුමිනියම් කනෙක්ටර්  බුරුල් වීම"},

            {"77", "0",  "5", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "11", "5", "Burnt meter / Burnt Meter terminal", "මනුව පිළිස්සීම/ මනු ටර්මිනල් පිළිස්සීම"},
            {"77", "12", "5", "Damage meter", "මනුවට හානි සිදුවීම"},
            {"77", "13", "5", "Loose meter terminal connection", "මනු  කනෙක්ටර් බුරුල් වීම"},

            {"77", "0", "6", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "14", "6", "Burnt / Loose Cutout/ Loose cutout elements", "මනු Fuse ටර්මිනල් පිළිස්සීම/ මනු Fuse ලුහු සම්බන්ධතා / Fuse උපාංග ගැළවීම"},
            {"77", "15", "6", "Fusing", "මනු Fuse දැවී යාම"},
            {"77", "16", "6", "Damage Cutout/No Cutout", "කටවුට් වලට හානි සිදුවීම / කටවුට් නොමැති වීම"},

            {"77", "0", "7", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "17", "7", "Burnt MCCB", "MCCB පිළිස්සීම"},
            {"77", "18", "7", "Terminal connection failures", "MCCB හානි වීම / පාදම සම්බන්ධතා බිඳවැටුම්"},
            {"77", "19", "7", "MCCB Overloading", "MCCB අධිබැර වීම"},

            {"77", "0", "8", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "20", "8", "Non", "කිසිවක් නැත"},

            {"77", "0", "9", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "21", "9", "Conductor touching", "කම්බි පැටලී තිබිම"},
            {"77", "22", "9", "Broken conductor", "කම්බි කැඩි තිබිම"},
            {"77", "23", "9", "Wire Damage by tree cutting / Human Activities", "ගස් අතු කැපීම මගින්  කම්බි හානි කිරීම"},

            {"77", "0", "10","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "24", "10","Aluminium Loose/Burnt/Damage jumper connection", "ඇළුමිනියම් ලිහිල් / පිළිස්සු/  හානී වු ජම්පර්"},
            {"77", "25", "10","ABC Loose/ Burnt/ Piercing/Sleeves",  "ABC ලිහිල් / පිළිස්සු කනෙක්ටර්"},
            {"77", "26", "10","Bimetallic Jumper Breakdowns",  "බයිමෙටලික් කනෙක්ටර් ආශ්\u200Dරිත බිඳවැටුම්"},

            {"77", "0", "11","Please select", "කරුණාකර තෝරන්න"},
            {"77", "27","11", "LV Terminal Loose Connection",  "තාරාපැවියේ LV ටර්මිනලයේ ලිහිල් සම්බන්ධය"},
            {"77", "28","11", "Load Wire & Fuse Terminal Failures",  "බැර තත් සහ Fuse සන්ධි බිඳවැටුම්"},

            {"77", "0", "12","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "29", "12","Broken/ Perished conductor/Touching/Mid span joint failures", "කම්බි දිරීම සහ කැඩීම , එකිනෙක ගැටීම, මිඩ්ස්පෑන් ජොයින්\u200Dට් බිඳවැටුම්"},

            {"77", "0", "13","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "30", "13","Skew/Broken/Damage/Perished Pole", "කණු  ඇලවිම , කැඩීම, අලාභහානි වීම, දිරාපත්වීම"},

            {"77", "0",  "14", "Please select","කරුණාකර තෝරන්න"},
            {"77", "31",  "14", "Fusing/MCCB Tripping", "Fuse දැවීම . MCCB ක්\u200Dරියාත්මක වීම. "},
            {"77", "32", "14",  "Fuse Base damage/Fuse terminal burnt/Burnt MCCB", "Fuse බේස් / ටර්මිනල් පිලිස්සීම්"},
            {"77", "33", "14",  "Fuse Base Damage / Burnt due to tree branch fallen down",  "ගස් අතු වැටීම නිසා Fuse බේස් / ටර්මිනල් පිලිස්සීම්"},

            {"77", "0","15", "Please select",  "කරුණාකර තෝරන්න"},
            {"77", "34", "15","Cable Burnt/Joint Failures (LT)", "LT කේබල් ජොඉන්ට් ආශ්\u200Dරිත බිඳවැටුම්"},

            {"77", "0", "16","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "35","16", "Malpractice", "වැරදි ක්\u200Dරම"},

            {"77", "0","17", "Please select",  "කරුණාකර තෝරන්න"},
            {"77", "36", "17","P/S Breaker Failures",  "ප්\u200Dරයිමරි සබ් බ්\u200Dරේකර් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "37", "17","P/S Bus bar Failures",  "ප්\u200Dරයිමරි සබ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "38", "17","P/S Cable end Failures",  "ප්\u200Dරයිමරි සබ් කේබල් එන්ඩ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "39", "17","UG Cable Failures 95 mm2",  "UG කේබල් 95 mm2 ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "40", "17","UG Cable Failures 240 mm2",  "UG කේබල් 240 mm2 ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "42", "17","Other", "වෙනත්"},

            {"77", "0", "18","Please select",  "කරුණාකර තෝරන්න"},
            {"77", "43","18", "P/S Breaker Failures", "ප්\u200Dරයිමරි සබ් බ්\u200Dරේකර් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "44","18", "P/S Bus bar Failures",  "ප්\u200Dරයිමරි සබ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "45","18", "P/S Cable end Failures",  "ප්\u200Dරයිමරි සබ් කේබල් එන්ඩ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "46", "18","UG Cable Failures 240 mm2", "UG කේබල් 240 mm2 ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "47","18", "DDLO Fuse Failures",  "DDLO Fuse පිළිස්සී තීබීම"},
            {"77", "48", "18","Other",  "වෙනත්"}};


////////////////////////////////////////////////////////////////////////////////////////////////////
public static String[][] FailureTypeList00 = {
        {"00", "0", "Please select"},
        {"00", "1", "Service Connection Failure"},
        {"00", "2", "LV Failure"},
        {"00", "3", "MV Failure"}
};



    public static String[][] FailureNatureList00 = {
            {"00", "0", "Please select", "1"},
    { "00", "1" ,"Service Wire Fault",			"1"},
    { "00", "2" ,"Cutout Failure",				"1"},
    { "00", "3" ,"Meter Fault",					"1"},
    { "00", "4" ,"MCB triped",					"1"},

            {"00", "0", "Please select", "2"},
    { "00", "5" ,"LT Fuse blown",				"2"},
    { "00", "6" ,"LT Feeder open cct",			"2"},
    { "00", "7" ,"LT high Voltage",				"2"},
    { "00", "8" ,"Transformer Failure ",		"2"},
    { "00", "9" ,"T/F- HT Fuse blown",			"2"},

            {"00", "0", "Please select", "3"},
    { "00", "10" ,"HT Fuse blown",				"3"},
    { "00", "11" ,"HT Feeder open cct",			"3"},
    { "00", "12" ,"CB/AR Tripping",				"3"},
    { "00", "13" ,"HV feeder Failure",			"3"}};




    public static String[][] FailureCauseList00 = {

            {"00", "0", "Please select", "1"},
    { "00", "1" ,"Lightning",										"1"},
    { "00", "2" ,"Cable / Conductor brakeage- Due to Wayleaves",	"1"},
    { "00", "3" ,"Cable / Conductor brakeage- Due to Vehicle",		"1"},
    { "00", "4" ,"Illegal tapping",									"1"},
    { "00", "5" ,"Tapping Point failure",							"1"},
    { "00", "6" ,"Other",											"1"},

            {"00", "0", "Please select", "2"},
    { "00", "7" ,"Lightning",										"2"},
    { "00", "8" ,"Loose end termination",							"2"},
    { "00", "9" ,"Over load",										"2"},
    { "00", "10" ,"Consumer side fault",							"2"},
    { "00", "11" ,"Oxide in the termination",						"2"},

            {"00", "0", "Please select", "3"},
    { "00", "12" ,"Lightning",										"3"},
    { "00", "13" ,"Meter Failure",									"3"},
    { "00", "14" ,"Meter tampering",								"3"},
    { "00", "15" ,"Loose end termination",							"3"},
    { "00", "16" ,"Other",											"3"},
    { "00", "17" ,"Oxide in the termination",						"3"},

            {"00", "0", "Please select", "4"},
    { "00", "18" ,"Lightning",										"4"},
    { "00", "19" ,"Loose end termination",							"4"},
    { "00", "20" ,"Over load",										"4"},
    { "00", "21" ,"Consumer side fault",							"4"},
    { "00", "22" ,"Oxide in the termination",						"4"},

            {"00", "0", "Please select", "5"},
    { "00", "23" ,"Accidents due to vehicle",						"5"},
    { "00", "24" ,"Lightning",										"5"},
    { "00", "25" ,"Branches coming from distance",					"5"},
    { "00", "26" ,"Burnt tail wires and cables",					"5"},
    { "00", "27" ,"Cracked insulators",								"5"},
    { "00", "28" ,"Due to animals and birds",						"5"},
    { "00", "29" ,"Due to broken poles",							"5"},
    { "00", "30" ,"Loose span and entanglement",					"5"},
    { "00", "31" ,"LT side Over load",								"5"},
    { "00", "32" ,"Other",											"5"},
    { "00", "33" ,"Vegetations",									"5"},

            {"00", "0", "Please select", "6"},
    { "00", "34" ,"Branches coming from distance",					"6"},
    { "00", "35" ,"Burnt tail wires and cables",					"6"},
    { "00", "36" ,"Cable / Conductor brakeage",						"6"},
    { "00", "37" ,"Midspan joint failure",							"6"},
    { "00", "38" ,"Jumper point failure",							"6"},
    { "00", "39" ,"Other",											"6"},

            {"00", "0", "Please select", "7"},
    { "00", "40" ,"Lightning",										"7"},
    { "00", "41" ,"Floating Neutral",								"7"},
    { "00", "42" ,"Neutral leakage",								"7"},
    { "00", "43" ,"Other",											"7"},

            {"00", "0", "Please select", "8"},
    { "00", "44" ,"Lightning",										"8"},
    { "00", "45" ,"Oil leakage",									"8"},
    { "00", "46" ,"Transformer bushings failure",					"8"},
    { "00", "47" ,"Due to animals and birds",						"8"},
    { "00", "48" ,"Other",											"8"},
    { "00", "49" ,"LT side Over load",								"8"},
    { "00", "50" ,"Transformer internal fault",						"8"},

            {"00", "0", "Please select", "9"},
    { "00", "51" ,"Lightning",										"9"},
    { "00", "52" ,"Incorrect fusing",								"9"},
    { "00", "53" ,"Other",											"9"},
    { "00", "54" ,"LT side Over load",								"9"},
    { "00", "55" ,"Due to animals and birds",						"9"},
    { "00", "56" ,"Transformer internal fault",						"9"},

            {"00", "0", "Please select", "10"},
    { "00", "57" ,"Accidents due to vehicle",						"10"},
    { "00", "58" ,"Lightning",										"10"},
    { "00", "59" ,"Branches coming from distance",					"10"},
    { "00", "60" ,"Midspan joint failure",							"10"},
    { "00", "61" ,"Jumper point failure",							"10"},
    { "00", "62" ,"Cracked insulators",								"10"},
    { "00", "63" ,"Cable / Conductor brakeage",						"10"},
    { "00", "64" ,"Due to broken poles",							"10"},
    { "00", "65" ,"Other",											"10"},
    { "00", "66" ,"Incorrect fusing",								"10"},
    { "00", "67" ,"Due to animals and birds",						"10"},
    { "00", "68" ,"Vegetations",									"10"},

            {"00", "0", "Please select", "11"},
    { "00", "69" ,"Midspan joint failure",							"11"},
    { "00", "70" ,"Jumper point failure",							"11"},
    { "00", "71" ,"Cable / Conductor brakeage",						"11"},
    { "00", "72" ,"Burnt tail wires and cables",					"11"},
    { "00", "73" ,"Branches coming from distance",					"11"},
    { "00", "74" ,"Other",											"11"},

            {"00", "0", "Please select", "12"},
    { "00", "75" ,"Accidents due to vehicle",						"12"},
    { "00", "76" ,"Lightning",										"12"},
    { "00", "77" ,"Branches coming from distance",					"12"},
    { "00", "78" ,"Burnt jumpers and conductors",					"12"},
    { "00", "79" ,"Cracked insulators",								"12"},
    { "00", "80" ,"Due to broken poles",							"12"},
    { "00", "81" ,"Other",											"12"},
    { "00", "82" ,"Over load",										"12"},
    { "00", "83" ,"Vegetations",									"12"},

            {"00", "0", "Please select", "13"},
    { "00", "84" ,"Total System Power failure",						"13"},
    { "00", "85" ,"Field Installed Switchgear failure",				"13"},
    { "00", "86" ,"Grid installed switchgear failure",				"13"}};

////////////////////////////////////////////////////////////////////////////////////////////////////
public static String[][] GetFailureTypeList(Context context){
        final String area_id = ReadStringPreferences(context, "area_id","");
        if(area_id.equals("77")){
            return FailureTypeList77;
        }else{
            return FailureTypeList00;
        }
    }

    public static String[][] GetFailureCauseList(Context context){
        final String area_id = ReadStringPreferences(context, "area_id","");
        if(area_id.equals("77")){
            return FailureCauseList77;
        }else{
            return FailureCauseList00;
        }
    }

    public static String[][] GetFailureNatureList(Context context){
        final String area_id = ReadStringPreferences(context, "area_id","");
        if(area_id.equals("77")){
            return FailureNatureList77;
        }else{
            return FailureNatureList00;
        }
    }


    private static String ReadStringPreferences(Context context, String key, String defaultValue){
        SharedPreferences prfs = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        return prfs.getString(key, defaultValue);
    }
}
