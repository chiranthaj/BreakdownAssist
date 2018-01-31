package lk.steps.breakdownassistpluss;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JagathPrasanga on 2017-06-23.
 */

public class Strings {

    public static String GetStatusName(int StatusId){
       // int StatusId = Integer.parseInt(statusId);
        if(StatusId == Breakdown.JOB_DELIVERED) return "Unattained";
        else if(StatusId == Breakdown.JOB_ACKNOWLEDGED) return "Unattained";
        else if(StatusId == Breakdown.JOB_VISITED) return "VisitedDialog";
        else if(StatusId == Breakdown.JOB_ATTENDING) return "Attending";
        else if(StatusId == Breakdown.JOB_TEMPORARY_COMPLETED) return "Temporary completed";
        else if(StatusId == Breakdown.JOB_COMPLETED) return "Completed";
        else if(StatusId == Breakdown.JOB_WITHDRAWN) return "Withdrawn";
        else if(StatusId == Breakdown.JOB_REJECT) return "Rejected";
        else if(StatusId == Breakdown.JOB_RE_CALLED) return "Re-called";
        else if(StatusId == Breakdown.JOB_RETURNED) return "Returned";
        else if(StatusId == Breakdown.JOB_FORWARDED) return "Forwarded";
        else return "Undefined";
    }

    public static String[][] VisitedComments = {
            {"-", "0", "-","Please select","කරුණාකර තෝරන්න"},
            {"-", "1", "-","No required materials","අවශ්\u200Dය ද්\u200Dරව්\u200Dය නොමැත"},
            {"-", "2", "-","Time consuming","විශාල කාලයක් අවශ්\u200Dයයි"},
            {"-", "3", "-","Other","වෙනත්"}
    };

    public static String[] AttendingComments = {
            "Please select",
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
    };

    public static String[][] NotAttendingComments = {
            {"-", "0", "-","Please select","කරුණාකර තෝරන්න"},
            {"-", "1", "-","Another breakdown","වෙනත් බිඳවැටුමක්"},
            {"-", "2", "-","No required materials","අවශ්\u200Dය ද්\u200Dරව්\u200Dය නොමැත"},
            {"-", "3", "-","Time consuming","විශාල කාලයක් අවශ්\u200Dයයි"},
    };

    public static String[][] DoneComments = {
            {"-", "0", "-","Please select","කරුණාකර තෝරන්න"},
            {"-", "1", "-","No required materials","අවශ්\u200Dය ද්\u200Dරව්\u200Dය නොමැත"},
            {"-", "2", "-","Time consuming","විශාල කාලයක් අවශ්\u200Dයයි"},
           // {"-", "3", "-","Other","වෙනත්"}
    };

    public static String[][] RejectComments = {
            {"-", "0", "-","Please select","කරුණාකර තෝරන්න"},
            {"-", "1", "-","Internal fault","ගෘහ අභ්‍යන්තර දෝෂයකි"},
            {"-", "2", "-","No fault","දෝෂයක් නැත"},
           // {"-", "3", "-","Other","වෙනත්"}
    };

    public static String[] CompletedComments = {
            "Please select",
            "Comment1",
            "Comment2",
            "Comment3",
            "Custom",
    };

    public static String[][] ReturnComments = {
            {"-", "0", "-","Please select","කරුණාකර තෝරන්න"},
            {"-", "0", "-","Not belongs to the Area","වෙනත් ප්\u200Dරදේශයකට අයත්ය"},
            {"-", "0", "-","Not belongs to the ECSC","වෙනත් ඩිපෝවකට අයත්ය"},
            {"-", "0", "-","Not belongs to the Team","වෙනත් කණ්ඩායමකට අයත්ය"},
    };

    public static String[][] ComplainTypeList00 = {
            {"-", "1", "-","Supply failed at premises","නිවසේ විදුලිය නැත"},
            {"-", "2", "-","Supply failed in area","ප්\u200Dරදේශයේ විදුලිය නැත"},
            {"-", "3", "-","Broken service wire","සවිස් වයරය කැඩී ඇත"},
            {"-", "4", "-", "Abnormal/ Low Voltage","අසාමන්\u200Dය / අඩු වෝල්ටීය තාවයක්"},
            {"-", "5", "-","Conductor Burning","වයර් පිළිස්සී ඇත"},
            {"-", "6", "-","Flood","ග0වතුර"},
            {"-", "7", "-","Flashing Insulators","ඉන්සුලේටර් විනාශ වී ඇත"},
            {"-", "8", "-","High Voltage","අධි වෝල්ටීය තාවයක්"},
            {"-", "9", "-","Meter Burning","මීටර්ය පිළිස්සීමක්"},
            {"-", "10", "-","Road accident cause pole damage","රිය අනතුරක් නිස කණු කැඩීමක්"},
            {"-", "11", "-","Road accident cause Cable / Conductor damage","රිය අනතුරක් නිස වයර් කැඩීමක්"},
            {"-", "12", "-","Tree has fallen on to the line","ගසක් වැටීමක්"},
            {"-", "13", "-","Other","වෙනත්"}
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////


    //[AreaId], [FailureTypeId], [FailureTypeName]
    public static String[][] FailureTypeList77 = {
            {"77", "0", "-", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "1", "-", "Service Connection Strings", "සේවා සැපයුම් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "2", "-", "LV Strings", "LV බිඳවැටුම්"},
            {"77", "3", "-", "MV Strings", "MV බිඳවැටුම්"}};


    //[AreaId], [FailureNatureId], [FailureNatureName], [ParentFailureTypeId]
    public static String[][] FailureNatureList77 = {
            {"77", "0", "1", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "1", "1", "Service wire breakdowns", "සේවා රැහැන ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "2", "1", "Aluminum Breakdowns", "ඇළුමිනියම් රැහැන් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "3", "1", "ABC Breakdowns", "ABC රැහැන් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "4", "1", "Bimetallic Breakdowns", "බයිමෙටලික් ආශ්\u200Dරිත බිඳවැටුම්."},
            {"77", "5", "1", "Meter Breakdowns", "මනුව ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "6", "1", "Cut Out Terminal Breakdowns", "මනු කටවුට් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "7", "1", "MCCB Failures", "MCCB ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "8", "1", "Other", "වෙනත්"},

            {"77", "0", "2", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "9", "2", "Failures due to Tree / Branch fallen Down", "ගස් අතු වැටීම නිසා සිදුවන හානි"},
            {"77", "10", "2", "Line Jumper Failures", "රැහැන් ජම්පර්  ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "11", "2", "Breakdowns in Substation", "තාරාපැවි ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "12", "2", "Conductor Breakdowns ", "කම්බි නඩත්තු නොකිරිම නිසා ඇතිවන බිඳවැටුම්"},
            {"77", "13", "2", "Pole Breakdowns", "කණු ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "14", "2", "Fuse", "Feeder Fuse ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "15", "2", "UG Cable Failures", "UG කේබල් බිඳවැටුම්"},
            {"77", "16", "2", "Other", "වෙනත්"},

            {"77", "0", "3", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "17", "3", "11kV", "11kV"},
            {"77", "18", "3", "33kV", "33kV"}};


    //[AreaId], [FailureCauseId], [FailureCauseName], [ParentFailureNatureId]
    public static String[][] FailureCauseList77 = {
            {"77", "0", "1", "Please select", "කරුණාකර තෝරන්න"},
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
            {"77", "10", "4", "Copper/ Aluminum loose connection", "කොපර් / ඇළුමිනියම් කනෙක්ටර්  බුරුල් වීම"},

            {"77", "0", "5", "Please select", "කරුණාකර තෝරන්න"},
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

            {"77", "0", "10", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "24", "10", "Aluminium Loose/Burnt/Damage jumper connection", "ඇළුමිනියම් ලිහිල් / පිළිස්සු/  හානී වු ජම්පර්"},
            {"77", "25", "10", "ABC Loose/ Burnt/ Piercing/Sleeves", "ABC ලිහිල් / පිළිස්සු කනෙක්ටර්"},
            {"77", "26", "10", "Bimetallic Jumper Breakdowns", "බයිමෙටලික් කනෙක්ටර් ආශ්\u200Dරිත බිඳවැටුම්"},

            {"77", "0", "11", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "27", "11", "LV Terminal Loose Connection", "තාරාපැවියේ LV ටර්මිනලයේ ලිහිල් සම්බන්ධය"},
            {"77", "28", "11", "Load Wire & Fuse Terminal Failures", "බැර තත් සහ Fuse සන්ධි බිඳවැටුම්"},

            {"77", "0", "12", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "29", "12", "Broken/ Perished conductor/Touching/Mid span joint failures", "කම්බි දිරීම සහ කැඩීම , එකිනෙක ගැටීම, මිඩ්ස්පෑන් ජොයින්\u200Dට් බිඳවැටුම්"},

            {"77", "0", "13", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "30", "13", "Skew/Broken/Damage/Perished Pole", "කණු  ඇලවිම, කැඩීම, අලාභහානි වීම, දිරාපත්වීම"},

            {"77", "0", "14", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "31", "14", "Fusing/MCCB Tripping", "Fuse දැවීම . MCCB ක්\u200Dරියාත්මක වීම. "},
            {"77", "32", "14", "Fuse Base damage/Fuse terminal burnt/Burnt MCCB", "Fuse බේස් / ටර්මිනල් පිලිස්සීම්"},
            {"77", "33", "14", "Fuse Base Damage / Burnt due to tree branch fallen down", "ගස් අතු වැටීම නිසා Fuse බේස් / ටර්මිනල් පිලිස්සීම්"},

            {"77", "0", "15", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "34", "15", "Cable Burnt/Joint Failures (LV)", "LV කේබල් ජොඉන්ට් ආශ්\u200Dරිත බිඳවැටුම්"},

            {"77", "0", "16", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "35", "16", "Malpractice", "වැරදි ක්\u200Dරම"},

            {"77", "0", "17", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "36", "17", "P/S Breaker Failures", "ප්\u200Dරයිමරි සබ් බ්\u200Dරේකර් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "37", "17", "P/S Bus bar Failures", "ප්\u200Dරයිමරි සබ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "38", "17", "P/S Cable end Failures", "ප්\u200Dරයිමරි සබ් කේබල් එන්ඩ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "39", "17", "UG Cable Failures 95 mm2", "UG කේබල් 95 mm2 ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "40", "17", "UG Cable Failures 240 mm2", "UG කේබල් 240 mm2 ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "42", "17", "Other", "වෙනත්"},

            {"77", "0", "18", "Please select", "කරුණාකර තෝරන්න"},
            {"77", "43", "18", "P/S Breaker Failures", "ප්\u200Dරයිමරි සබ් බ්\u200Dරේකර් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "44", "18", "P/S Bus bar Failures", "ප්\u200Dරයිමරි සබ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "45", "18", "P/S Cable end Failures", "ප්\u200Dරයිමරි සබ් කේබල් එන්ඩ් ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "46", "18", "UG Cable Failures 240 mm2", "UG කේබල් 240 mm2 ආශ්\u200Dරිත බිඳවැටුම්"},
            {"77", "47", "18", "DDLO Fuse Failures", "DDLO Fuse පිළිස්සී තීබීම"},
            {"77", "48", "18", "Other", "වෙනත්"}};

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String[][] FailureTypeList00 = {
            {"00", "0", "-", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "1", "-", "Service Connection Strings", "සේවා සැපයුම් ආශ්‍රිත බිඳවැටුම්"},
            {"00", "2", "-", "LV Strings", "LV බිඳවැටුම්"},
            {"00", "3", "-", "MV Strings", "MV බිඳවැටුම්"}
    };


    public static String[][] FailureNatureList00 = {
            {"00", "0", "1", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "1", "1", "Service Wire Fault",  "සේවා සැපයුම් ආශ්‍රිත බිඳවැටුම් "},
            {"00", "2", "1", "Cut-Out Strings", "Cut-Out ආශ්‍රිත බිඳවැටුම්"},
            {"00", "3", "1", "Meter Fault", " මනුව ආශ්‍රිත බිඳවැටුම්"},
            {"00", "4", "1", "MCB tripped", "MCB ආශ්‍රිත බිඳවැටුම්"},

            {"00", "0", "2", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "5", "2", "LV Fuse blown", " LV Fuse ආශ්‍රිත බිඳවැටුම්"},
            {"00", "6", "2", "LV Feeder open cct", "LV ෆීඩර් විසන්ධි වීම් "},
            {"00", "7", "2", "LV high Voltage", "LV හයි වෝල්ටේජ් ෆීඩර් විසන්ධි වීම්"},
            {"00", "8", "2", "Transformer Strings", "ට්‍රාන්ස්ෆෝමර් ආශ්‍රිත බිඳවැටුම්"},
            {"00", "9", "2", "T/F- MV Fuse blown", "T/F- MV Fuse ආශ්‍රිත බිඳවැටුම්"},

            {"00", "0", "3", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "10", "3", "MV Fuse blown", "MV Fuse ආශ්‍රිත බිඳවැටුම්"},
            {"00", "11", "3", "MV Feeder open circuit", "MV ෆීඩර් විසන්ධි වීම්"},
            {"00", "12", "3", "CB/AR Tripping", "CB/AR ට්‍රිපින් "},
            {"00", "13", "3", "MV feeder Strings", "MV ෆීඩර් ආශ්‍රිත බිඳවැටුම්"}};


    public static String[][] FailureCauseList00 = {

            {"00", "0", "1", "Please select",               "කරුණාකර තෝරන්න"},
            {"00", "1", "1", "Lightning",                   "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "2", "1", "Broken Cable / Conductor - Due to Wayleaves", "ගස්අතු කැපීම නිසා සේවා රැහැන් කැඩීම"},
            {"00", "3", "1", "Broken Cable / Conductor - Due to Vehicle", "වාහන වැදීම නිසා සේවා රැහැන් කැඩීම"},
            {"00", "4", "1", "Illegal tapping", "නීති විරෝධී ටැප් කිරීම්"},
            {"00", "5", "1", "Tapping Point failure", "ටැපින් පොයින්ට් ආශ්‍රිත බිඳවැටුම්"},
            {"00", "6", "1", "Other", "වෙනත් බිඳවැටුම්"},

            {"00", "0", "2", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "7", "2", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "8", "2", "Loose end-termination", "ලුහු end-termination "},
            {"00", "9", "2", "Over load", "අධිබැර වීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "10", "2", "Internal fault", "ගෘහ අභ්‍යන්තර දෝෂ"},
            {"00", "11", "2", "Oxide in the termination", "Termination ඹක්සයිඩ බැඳී තිබීම"},

            {"00", "0", "3", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "12", "3", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "13", "3", "Meter Strings", "මනුව ආශ්‍රිත බිඳවැටුම්"},
            {"00", "14", "3", "Meter tampering", "මනුව මඟින් නීති විරෝධී ලෙස විදුලිය ලබා ගැනීම"},
            {"00", "15", "3", "Loose end-termination", "ලුහු end-termination"},
            {"00", "16", "3", "Other", "වෙනත් බිඳවැටුම්"},
            {"00", "17", "3", "Oxide in the termination", "Termination ඹක්සයිඩ බැඳී තිබීම"},

            {"00", "0", "4", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "18", "4", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "19", "4", "Loose end termination", "ලුහු end-termination"},
            {"00", "20", "4", "Over load", "අධිබැර වීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "21", "4", "Internal fault", "ගෘහ අභ්‍යන්තර දෝෂ"},
            {"00", "22", "4", "Oxide in the termination", "Termination ඹක්සයිඩ බැඳී තිබීම"},

            {"00", "0", "5", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "23", "5", "Accidents due to vehicle", "වාහන අනතුරු නිසා සිදුවන බිඳවැටුම්"},
            {"00", "24", "5", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "25", "5", "Branches coming from distance", "රැහැන් මතට ගස් අතු වැටීම"},
            {"00", "26", "5", "Burnt tail wires and cables", "පිළිස්සුණු tail  wires සහ cables"},
            {"00", "27", "5", "Cracked insulators", "පළුදු වූ insulators "},
            {"00", "28", "5", "Due to animals and birds", "සතුන් සහ කුරුල්ලන් වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "29", "5", "Due to broken poles", "කණු කැඩීම නිසා ඇතිවන බිඳවැටුම්"},
            {"00", "30", "5", "Loose span and entanglement", "ලුහු span and entanglement"},
            {"00", "31", "5", "LV side Over load", "LV අධීබැර වීම්"},
            {"00", "32", "5", "Other", "වෙනත් බිඳවැටුම්"},
            {"00", "33", "5", "Vegetations", "ගස් අතු වැදීම නිසා"},

            {"00", "0", "6", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "34", "6", "Branches coming from distance", "රැහැන් මතට ගස් අතු වැටීම"},
            {"00", "35", "6", "Burnt tail wires and cables", "පිළිස්සුණු tail  wires සහ cables"},
            {"00", "36", "6", "Cable / Conductor broken", "කැඩුණු Cable / Conductor "},
            {"00", "37", "6", "Midspan joint failure", "Midspan joint ආශ්‍රිත බිඳවැටුම් "},
            {"00", "38", "6", "Jumper point failure", "ජම්පර් ආශ්‍රිත බිඳවැටුම්"},
            {"00", "39", "6", "Other", "වෙනත් බිඳවැටුම්"},

            {"00", "0", "7", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "40", "7", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "41", "7", "Floating Neutral", "නියුට්‍රල් බිඳවැටුම්"},
            {"00", "42", "7", "Neutral leakage", "නියුට්‍රල් කාන්දුවීම්"},
            {"00", "43", "7", "Other", "වෙනත් බිඳවැටුම්"},

            {"00", "0", "8", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "44", "8", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "45", "8", "Oil leakage", "තෙල් කාන්දුවීම්"},
            {"00", "46", "8", "Transformer bushings failure", "ට්‍රාන්ස්ෆෝමර් බුෂ් ආශ්‍රිත බිඳවැටුම් "},
            {"00", "47", "8", "Due to animals and birds", "සතුන් සහ කුරුල්ලන් වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "48", "8", "Other", "වෙනත් බිඳවැටුම්"},
            {"00", "49", "8", "LV side Over load", "LV අධීබැර වීම් "},
            {"00", "50", "8", "Transformer internal fault", "ට්‍රාන්ස්ෆෝමර් අභ්‍යන්තර දෝෂ"},

            {"00", "0", "9", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "51", "9", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "52", "9", "Incorrect fusing", "වැරදි පුළුසු යෙදීම නිසා"},
            {"00", "53", "9", "Other", "වෙනත් බිඳවැටුම්"},
            {"00", "54", "9", "LV side Over load", "LV අධීබැර වීම් "},
            {"00", "55", "9", "Due to animals and birds", "සතුන් සහ කුරුල්ලන් වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "56", "9", "Transformer internal fault", "ට්‍රාන්ස්ෆෝමර් අභ්‍යන්තර දෝෂ"},

            {"00", "0", "10", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "57", "10", "Accidents due to vehicle", "වාහන අනතුරු නිසා සිදුවන බිඳවැටුම්"},
            {"00", "58", "10", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "59", "10", "Branches coming from distance", "රැහැන් මතට ගස් අතු වැටීම"},
            {"00", "60", "10", "Midspan joint failure", "Midspan joint ආශ්‍රිත බිඳවැටුම් "},
            {"00", "61", "10", "Jumper point failure", "ජම්පර් ආශ්‍රිත බිඳවැටුම්"},
            {"00", "62", "10", "Cracked insulators", "පළුදු වූ insulators"},
            {"00", "63", "10", "Cable / Conductor broken", "කැඩුණු Cable / Conductor "},
            {"00", "64", "10", "Due to broken poles", "කණු කැඩීම නිසා ඇතිවන බිඳවැටුම්"},
            {"00", "65", "10", "Other", "වෙනත් බිඳවැටුම්"},
            {"00", "66", "10", "Incorrect fusing", "වැරදි පුළුසු යෙදීම නිසා"},
            {"00", "67", "10", "Due to animals and birds", "සතුන් සහ කුරුල්ලන් වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "68", "10", "Vegetations", "ගස් අතු වැදීම නිසා"},

            {"00", "0", "11", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "69", "11", "Midspan joint failure", " Midspan joint ආශ්‍රිත බිඳවැටුම් "},
            {"00", "70", "11", "Jumper point failure", "ජම්පර් ආශ්‍රිත බිඳවැටුම්"},
            {"00", "71", "11", "Cable / Conductor broken", "කැඩුණු Cable / Conductor"},
            {"00", "72", "11", "Burnt tail wires and cables", "පිළීස්සුණු tail wires and cables "},
            {"00", "73", "11", "Branches coming from distance", "රැහැන් මතට ගස් අතු වැටීම"},
            {"00", "74", "11", "Other", "වෙනත් බිඳවැටුම්"},

            {"00", "0", "12", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "75", "12", "Accidents due to vehicle", "වාහන අනතුරු නිසා සිදුවන බිඳවැටුම්"},
            {"00", "76", "12", "Lightning", "අකුණු සැර වැදීම නිසා ඇති වන බිඳවැටුම්"},
            {"00", "77", "12", "Branches coming from distance", "රැහැන් මතට ගස් අතු වැටීම"},
            {"00", "78", "12", "Burnt jumpers and conductors", "පිළීස්සුණු jumpers and conductors"},
            {"00", "79", "12", "Cracked insulators", "පළුදු වූ insulators"},
            {"00", "80", "12", "Due to broken poles", "කණු කැඩීම නිසා සිදුවන බිඳවැටුම්"},
            {"00", "81", "12", "Other", "වෙනත් බිඳවැටුම්"},
            {"00", "82", "12", "Over load", "අධීබැර වීම නිසා සිදුවන බිඳවැටුම්"},
            {"00", "83", "12", "Vegetations", "ගස් අතු වැදීම නිසා"},

            {"00", "0", "13", "Please select", "කරුණාකර තෝරන්න"},
            {"00", "84", "13", "Total System Power failure", "සමස්ථ පද්ධතිය බිඳවැටීම "},
            {"00", "85", "13", "Field Installed Switchgear failure", "Field Installed Switchgear failure"},
            {"00", "86", "13", "Grid installed switchgear failure", "Grid installed switchgear failure "}};



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String[][] GetFailureTypeList(Context context) {
        final String area_id = Common.ReadStringPreferences(context, "area_id", "");
        if (area_id.equals("77")) {
            return FailureTypeList77;
        } else {
            return FailureTypeList00;
        }
    }

    public static String[][] GetFailureCauseList(Context context) {
        final String area_id = Common.ReadStringPreferences(context, "area_id", "");
        if (area_id.equals("77")) {
            return FailureCauseList77;
        } else {
            return FailureCauseList00;
        }
    }

    public static String[][] GetFailureNatureList(Context context) {
        final String area_id = Common.ReadStringPreferences(context, "area_id", "");
        if (area_id.equals("77")) {
            return FailureNatureList77;
        } else {
            return FailureNatureList00;
        }
    }


    public static String GetDescription(String text){
        for(int i=0;i< Strings.ComplainTypeList00.length;i++){
            if(Strings.ComplainTypeList00[i][3].equals(text))
                return Strings.ComplainTypeList00[i][4];
        }
        return text;
    }
}
