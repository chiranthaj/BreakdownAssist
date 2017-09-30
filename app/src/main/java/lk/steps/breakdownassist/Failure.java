package lk.steps.breakdownassist;

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
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
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
            "Custom",
    };
    public static String[] CompletedComments = {
            "Please select",
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////


    //[AreaId], [FailureTypeId], [FailureTypeName]
    public static String[][] FailureTypeList = {
            {"77", "0", "Please select"},
            {"77", "1", "Service Connection Failure"},
            {"77", "2", "LV Failure"},
            {"77", "3", "HT Failure"}};


    //[AreaId], [FailureNatureId], [FailureNatureName], [ParentFailureTypeId]
    public static String[][] FailureNatureList = {
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

            {"77", "16", "11KV / 33KV", "3"}};

    //[AreaId], [FailureCauseId], [FailureCauseName], [ParentFailureNatureId]
    public static String[][] FailureCauseList = {
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
            {"77", "28", "Load Wire && Fuse Terminal Failures", "11"},

            {"77", "29", "Broken/ Perished conductor/Touching/Mid span joint failures", "12"},

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


    public static String[][] ComplainTypeList = {
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


}
