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
            {"77", "0", "Please select"},
            {"77", "1", "Service Connection Failure"},
            {"77", "2", "LV Failure"},
            {"77", "3", "HT Failure"}};


    //[AreaId], [FailureNatureId], [FailureNatureName], [ParentFailureTypeId]
    public static String[][] FailureNatureList77 = {
            {"77", "0", "Please select", "1"},
            {"77", "1", "Service wire breakdowns", "1"},
            {"77", "2", "Aluminum Breakdowns", "1"},
            {"77", "3", "ABC Breakdowns", "1"},
            {"77", "4", "Bimetallic Breakdowns", "1"},
            {"77", "5", "Meter Breakdowns", "1"},
            {"77", "6", "Cut Out Terminal Breakdowns", "1"},
            {"77", "7", "MCCB Failures", "1"},

            {"77", "0", "Please select", "2"},
            {"77", "8", "Failures due to Tree / Branch fallen Down", "2"},
            {"77", "9", "Line Jumper Failures", "2"},
            {"77", "10", "Breakdowns in Substation", "2"},
            {"77", "11", "Conductor Breakdowns ", "2"},
            {"77", "12", "Pole Breakdowns", "2"},
            {"77", "13", "Fuse", "2"},
            {"77", "14", "UG Cable Failures", "2"},
            {"77", "15", "Others", "2"},

            {"77", "0", "Please select", "3"},
            {"77", "16", "11KV / 33KV", "3"}};

    //[AreaId], [FailureCauseId], [FailureCauseName], [ParentFailureNatureId]
    public static String[][] FailureCauseList77 = {
            {"77", "0", "Please select", "1"},
            {"77", "1", "Broken Wire / Loose connection due to monkey", "1"},
            {"77", "2", "Burnt Service Wire / Loop Service Breakdowns", "1"},
            {"77", "3", "Wire damage due to Tree/Branch Fallen Down", "1"},
            {"77", "4", "Wire damage due to human activities", "1"},

            {"77", "0", "Please select", "2"},
            {"77", "5", "No H Connectors", "2"},
            {"77", "6", "Loose / Burnt Service connection", "2"},

            {"77", "0", "Please select", "3"},
            {"77", "7", "No  ABC Connectors", "3"},
            {"77", "8", "Burnt ABC  Connectors", "3"},

            {"77", "0", "Please select", "4"},
            {"77", "9", "Copper / Aluminum Oxidation - From the pole", "4"},
            {"77", "10", "Copper/ Aluminum loose connection", "4"},

            {"77", "0", "Please select", "5"},
            {"77", "11", "Burnt meter / Burnt Meter terminal", "5"},
            {"77", "12", "Damage meter", "5"},
            {"77", "13", "Loose meter terminal connection", "5"},

            {"77", "0", "Please select", "6"},
            {"77", "14", "Burnt Cutout/ Loose Cutout/ Loose cutout elements", "6"},
            {"77", "15", "Fusing", "6"},
            {"77", "16", "Damage Cutout/No Cutout", "6"},

            {"77", "0", "Please select", "7"},
            {"77", "17", "Burnt MCCB", "7"},
            {"77", "18", "Terminal connection failures", "7"},
            {"77", "19", "MCCB Overloading", "7"},

            {"77", "0", "Please select", "8"},
            {"77", "20", "Non", "8"},

            {"77", "0", "Please select", "9"},
            {"77", "21", "Conductor touching", "9"},
            {"77", "22", "Broken conductor", "9"},
            {"77", "23", "Wire Damage by tree cutting / Human Activities", "9"},

            {"77", "0", "Please select", "10"},
            {"77", "24", "Aluminium Loose/Burnt/Damage jumper connection", "10"},
            {"77", "25", "ABC Loose/ Burnt/ Piercing/Sleeves", "10"},
            {"77", "26", "Bimetallic Jumper Breakdowns", "10"},

            {"77", "0", "Please select", "11"},
            {"77", "27", "LV Terminal Loose Connection", "11"},
            {"77", "28", "Load Wire & Fuse Terminal Failures", "11"},

            {"77", "0", "Please select", "12"},
            {"77", "29", "Broken/ Perished conductor/Touching/Mid span joint failures", "12"},

            {"77", "0", "Please select", "13"},
            {"77", "30", "Skew/Broken/Damage/Perished Pole", "13"},

            {"77", "0", "Please select", "14"},
            {"77", "31", "Fusing/MCCB Tripping", "14"},
            {"77", "32", "Fuse Base damage/Fuse terminal burnt/Burnt MCCB", "14"},
            {"77", "33", "Fuse Base Damage / Burnt due to tree branch fallen down", "14"},

            {"77", "0", "Please select", "15"},
            {"77", "34", "Cable Burnt/Joint Failures (LT)", "15"},

            {"77", "0", "Please select", "16"},
            {"77", "35", "Malpractice", "16"},

            {"77", "0", "Please select", "17"},
            {"77", "36", "P/S Breaker Failures", "17"},
            {"77", "37", "P/S Bus bar Failures", "17"},
            {"77", "38", "P/S Cable end Failures", "17"},
            {"77", "39", "UG Cable Failures 95 mm2", "17"},
            {"77", "40", "UG Cable Failures 240 mm2", "17"},
            {"77", "41", "DDLO Fuse Failures", "17"},
            {"77", "42", "Other Failures", "17"}};


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
