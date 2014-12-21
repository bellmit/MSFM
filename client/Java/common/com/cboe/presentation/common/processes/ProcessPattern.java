/*
 * Created on Apr 21, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.common.processes;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

/**
 * @author I Nyoman Mahartayasa
 */
public class ProcessPattern
{
    private static ProcessPattern    instance;
    public static final String  SECTION="Pattern";
    public static final String  CAS_PATERN="CAS";
    public static final String  MDCAS_PATERN="MDCAS";
    public static final String  SACAS_PATERN="SACAS";
    public static final String  FIXCAS_PATERN="FIXCAS";
    public static final String  CFIX_PATERN="CFIX";
    public static final String  BC_PATERN="BC";
    public static final String  GC_PATERN="GC";
    public static final String  FRONTEND_PATERN="FRONTEND";
    public static final String  DN_PATERN="DN";
    public static final String  ICS_PATERN="ICS";
    public static final String  RT_SERVER_PATERN="RTSERVER";
    public static final String  XTP_PATERN="XTP";

    //backoffice
    public static final String  WBS_PATTERN="WBS";
    public static final String  MAD_PATTERN="MAD";
    public static final String  ITA_PATTERN="ITA";
    public static final String  TFL_PATTERN="TFL";
    public static final String  MR_PATTERN="MR";
    public static final String  FOCUS_PATTERN="FOCUS";
    public static final String  DCS_PATTERN="DCS";
    public static final String  MME_PATTERN="MME";
    public static final String  TPS_PATTERN="TPS";
    public static final String  BO_PATTERN="BO";
    public static final String  MPPI_PATTERN="MPPI";
    public static final String  ASAS_PATTERN="ASAS";
    public static final String  BJC_PATTERN="BJC";

    private static final String  PREFIX="$SBT_PREFIX";
    private static final String  HOST="$HOST_NAME";
    private static final String  ANY="[\\S\\d]*";
    
    
    private String casPattern;
    private String saCASPattern;
    private String mdCASPattern;
    private String fixCASPattern;
    private String cFixPattern;
    private String dnPattern;
    private String bcPattern;
    private String gcPattern;
    private String frontendPattern; 
    private String icsPattern;
    private String rtServerPattern;
    private String xtpPattern;
    private String wbsPattern;
    private String madPattern;
    private String itaPattern;
    private String tflPattern;
    private String mrPattern;
    private String focusPattern;
    private String dcsPattern;
    private String mmePattern;
    private String tpsPattern;
    private String boPattern;
    private String mppiPattern;
    private String asasPattern;
    private String bjcPattern;
    
    public static ProcessPattern getInstance()
    {
        if (instance == null)
        {
            instance = new ProcessPattern();
        }
        return instance;
    }
    
    private ProcessPattern()
    {
        casPattern      = PREFIX+"cas[\\d]+[v]"+ANY+HOST;
        saCASPattern    = PREFIX+"sacas[\\d]+[v]"+ANY+HOST;
        mdCASPattern    = PREFIX+"mdcas[\\d]+[v]"+ANY+HOST;
        fixCASPattern   = PREFIX+"fixcas[\\d]+[v]"+ANY+HOST;
        cFixPattern     = PREFIX+"cfix[\\d]+[v]"+ANY+HOST;
        dnPattern       = PREFIX+ANY+"[Dd][Nn]"+ANY;
        bcPattern       = PREFIX+ANY+"[Bb][Cc]"+ANY;
        gcPattern       = PREFIX+ANY+"[Gg][Cc]"+ANY;
        frontendPattern = PREFIX+ANY+"[Ff]rontend"+ANY; 
        icsPattern      = PREFIX+"ics"+ANY;
        xtpPattern		= PREFIX+"-XTP"+ANY+"-XTP-"+HOST+ANY;
        rtServerPattern = "/_"+ANY;
        wbsPattern      = "[reg]?"+PREFIX+"WBServer"+ANY+HOST;
        madPattern      = "[reg]?"+PREFIX+"RegMadServer"+ANY+HOST;
        itaPattern      = "[reg]?"+PREFIX+"ITServer"+ANY+HOST;
        tflPattern      = "[reg]?"+PREFIX+"TFLServer"+ANY+HOST;
        mrPattern       = "[reg]?"+PREFIX+"MR"+ANY+HOST;
        focusPattern    = "[reg]?"+PREFIX+"FocusServer"+ANY+HOST;
        dcsPattern      = "[reg]?"+PREFIX+"DCServer"+ANY+HOST;
        mmePattern      = "[reg]?"+PREFIX+"MMEServer"+ANY+HOST;
        tpsPattern      = "[reg]?"+PREFIX+"TPSAppServer"+ANY+HOST;
        boPattern       = "[reg]?"+PREFIX+"BO"+ANY+HOST;
        mppiPattern     = "[reg]?"+PREFIX+"MPPI"+ANY+HOST;
        asasPattern     = "[reg]?"+PREFIX+"ASASAppServer"+ANY+HOST;
        bjcPattern      = "[reg]?"+PREFIX+"BJCAppServer"+ANY+HOST;
      

        String pattern = AppPropertiesFileFactory.find().getValue(SECTION,CAS_PATERN);
        if (pattern !=null)
        {
            casPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,SACAS_PATERN);
        if (pattern !=null)
        {
            saCASPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,MDCAS_PATERN);
        if (pattern !=null)
        {
            mdCASPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,FIXCAS_PATERN);
        if (pattern !=null)
        {
            fixCASPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,CFIX_PATERN);
        if (pattern !=null)
        {
            cFixPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,DN_PATERN);
        if (pattern !=null)
        {
            dnPattern = pattern;
        }

        pattern = AppPropertiesFileFactory.find().getValue(SECTION,BC_PATERN);
        if (pattern !=null)
        {
            bcPattern = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,GC_PATERN);
        if (pattern !=null)
        {
            gcPattern = pattern;
        }

        pattern = AppPropertiesFileFactory.find().getValue(SECTION,FRONTEND_PATERN);
        if (pattern !=null)
        {
            frontendPattern = pattern;
        }

        pattern = AppPropertiesFileFactory.find().getValue(SECTION,ICS_PATERN);
        if (pattern !=null)
        {
            icsPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION,XTP_PATERN);
        if (pattern !=null)
        {
            xtpPattern = pattern;
        }

        pattern = AppPropertiesFileFactory.find().getValue(SECTION,RT_SERVER_PATERN);
        if (pattern !=null)
        {
            rtServerPattern = pattern;
        }
        
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, WBS_PATTERN);
        if (pattern !=null)
        {
            wbsPattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, MAD_PATTERN);
        if (pattern !=null)
        {
            madPattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, ITA_PATTERN);
        if (pattern !=null)
        {
            itaPattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, TFL_PATTERN);
        if (pattern !=null)
        {
            tflPattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, MR_PATTERN);
        if (pattern !=null)
        {
            mrPattern       = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, FOCUS_PATTERN);
        if (pattern !=null)
        {
            focusPattern    = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, DCS_PATTERN);
        if (pattern !=null)
        {
            dcsPattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, MME_PATTERN);
        if (pattern !=null)
        {
            mmePattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, TPS_PATTERN);
        if (pattern !=null)
        {
            tpsPattern      = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, BO_PATTERN);
        if (pattern !=null)
        {
            boPattern       = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, MPPI_PATTERN);
        if (pattern !=null)
        {
            mppiPattern     = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, ASAS_PATTERN);
        if (pattern !=null)
        {
            asasPattern     = pattern;
        }
        pattern = AppPropertiesFileFactory.find().getValue(SECTION, BJC_PATTERN);
        if (pattern !=null)
        {
            bjcPattern      = pattern;
        }
    }
    
    public String getCASPattern(String prefix, String host){
        return casPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getSACASPattern(String prefix, String host){
        return saCASPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getMDCASPattern(String prefix, String host){
        return mdCASPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getFIXCASPattern(String prefix,String host)
    {
        return fixCASPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }

    public String getCFIXPattern(String prefix,String host)
    {
        return cFixPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
        
    public String getICSPattern(String prefix,String host)
    {
        return icsPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getDNPattern(String prefix,String host)
    {
        return dnPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getBCPattern(String prefix,String host)
    {
        return bcPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getGCPattern(String prefix,String host)
    {
        return gcPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getFrontendPattern(String prefix,String host)
    {
        return frontendPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }

    public String getRTServerPattern(String prefix,String host)
    {
        return rtServerPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }

    public String getRTServerPattern()
    {
        return rtServerPattern;
    }
    
    public String getXTPPattern(String prefix,String host)
    {
        return xtpPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    
    public String getWBSPattern(String prefix,String host)
    {
        return wbsPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }

    public String getMADPattern(String prefix,String host)
    {
        return madPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getITAPattern(String prefix,String host)
    {
        return itaPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getTFLPattern(String prefix,String host)
    {
        return tflPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getMRPattern(String prefix,String host)
    {
        return mrPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getFocusPattern(String prefix,String host)
    {
        return focusPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getDCSPattern(String prefix,String host)
    {
        return dcsPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getMMEPattern(String prefix,String host)
    {
        return mmePattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getTPSPattern(String prefix,String host)
    {
        return tpsPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getBOPattern(String prefix,String host)
    {
        return boPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getMPPIPattern(String prefix,String host)
    {
        return mppiPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getASASPattern(String prefix,String host)
    {
        return asasPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
    public String getBJCPattern(String prefix,String host)
    {
        return bjcPattern.replaceAll("\\"+PREFIX,prefix).replaceAll("\\"+HOST,host);
    }
}
