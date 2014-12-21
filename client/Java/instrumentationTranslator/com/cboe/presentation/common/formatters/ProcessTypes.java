/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.internalPresentation.common.formatters
 * User: torresl
 * Date: Jan 9, 2003 11:20:48 AM
 */
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.processes.ProcessInfoTypes;

public class ProcessTypes
{
    public static final int UNKNOWN_TYPE = ProcessInfoTypes.UNKNOWN_TYPE;
    public static final int CAS_TYPE     = ProcessInfoTypes.CAS_TYPE;
    public static final int FE_TYPE      = ProcessInfoTypes.FE_TYPE;
    public static final int DN_TYPE      = ProcessInfoTypes.DN_TYPE;
    public static final int BC_TYPE      = ProcessInfoTypes.BC_TYPE;
    public static final int SACAS_TYPE   = ProcessInfoTypes.SACAS_TYPE;
    public static final int GC_TYPE      = ProcessInfoTypes.GC_TYPE;
    public static final int FIXCAS_TYPE  = ProcessInfoTypes.FIXCAS_TYPE;
    public static final int MDCAS_TYPE   = ProcessInfoTypes.MDCAS_TYPE;
    public static final int CFIX_TYPE    = ProcessInfoTypes.CFIX_TYPE;
    public static final int ICS_TYPE     = ProcessInfoTypes.ICS_TYPE;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
    public static final int XTP_TYPE	 = ProcessInfoTypes.XTP_TYPE;

    public static final int WBS_TYPE    = ProcessInfoTypes.WBS_TYPE;
    public static final int MAD_TYPE    = ProcessInfoTypes.MAD_TYPE;
    public static final int ITA_TYPE    = ProcessInfoTypes.ITA_TYPE;
    public static final int TFL_TYPE    = ProcessInfoTypes.TFL_TYPE;
    public static final int MR_TYPE     = ProcessInfoTypes.MR_TYPE;
    public static final int FOCUS_TYPE  = ProcessInfoTypes.FOCUS_TYPE;
    public static final int DCS_TYPE    = ProcessInfoTypes.DCS_TYPE;
    public static final int MME_TYPE    = ProcessInfoTypes.MME_TYPE;
    public static final int TPS_TYPE    = ProcessInfoTypes.TPS_TYPE;
    public static final int BO_TYPE     = ProcessInfoTypes.BO_TYPE;
    public static final int MPPI_TYPE   = ProcessInfoTypes.MPPI_TYPE;
    public static final int ASAS_TYPE   = ProcessInfoTypes.ASAS_TYPE;
    public static final int BJC_TYPE    = ProcessInfoTypes.BJC_TYPE;

    public static final String  UNKNOWN_TYPE_STRING       = "Unknown";
    public static final String  CAS_TYPE_STRING           = "CAS";
    public static final String  FE_TYPE_STRING            = "FE";
    public static final String  DN_TYPE_STRING            = "DN";
    public static final String  BC_TYPE_STRING            = "BC";
    public static final String  SACAS_TYPE_STRING         = "SACAS";
    public static final String  GC_TYPE_STRING            = "GC";
    public static final String  FIXCAS_TYPE_STRING        = "FIXCAS";
    public static final String  MDCAS_TYPE_STRING         = "MDCAS";
    public static final String  CFIX_TYPE_STRING          = "CFIX";
    public static final String  ICS_TYPE_STRING           = "ICS";
    public static final String 	XTP_TYPE_STRING			  = "XTP";
    public static final String WBS_TYPE_STRING			  = "WBS";
    public static final String MAD_TYPE_STRING			  = "MAD";
    public static final String ITA_TYPE_STRING			  = "ITA";
    public static final String TFL_TYPE_STRING			  = "TFL";
    public static final String MR_TYPE_STRING			  = "MR";
    public static final String FOCUS_TYPE_STRING		  = "FOCUS";
    public static final String DCS_TYPE_STRING			  = "DCS";
    public static final String MME_TYPE_STRING			  = "MME";
    public static final String TPS_TYPE_STRING			  = "TPS";
    public static final String BO_TYPE_STRING			  = "BO";
    public static final String MPPI_TYPE_STRING			  = "MPPI";
    public static final String ASAS_TYPE_STRING			  = "ASAS";
    public static final String BJC_TYPE_STRING			  = "BJC";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String  INVALID_FORMAT = "INVALID_FORMAT";
    public static final String  INVALID_TYPE = "INVALID_TYPE";

    private ProcessTypes()
    {
    }
    public static String toString(int processType)
    {
        return toString(processType, TRADERS_FORMAT);
    }
    public static String toString(int processType, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(processType)
            {
                case UNKNOWN_TYPE:
                    return UNKNOWN_TYPE_STRING;
                case CAS_TYPE:
                    return CAS_TYPE_STRING;
                case FE_TYPE:
                    return FE_TYPE_STRING;
                case DN_TYPE:
                    return DN_TYPE_STRING;
                case BC_TYPE:
                    return BC_TYPE_STRING;
                case SACAS_TYPE:
                    return SACAS_TYPE_STRING;
                case GC_TYPE:
                    return GC_TYPE_STRING;
                case FIXCAS_TYPE:
                    return FIXCAS_TYPE_STRING;
                case MDCAS_TYPE:
                    return MDCAS_TYPE_STRING;
                case CFIX_TYPE:
                    return CFIX_TYPE_STRING;
                case ICS_TYPE:
                    return ICS_TYPE_STRING;
                case XTP_TYPE:
                    return XTP_TYPE_STRING;
                case WBS_TYPE:
                    return WBS_TYPE_STRING;
                case MAD_TYPE:
                    return MAD_TYPE_STRING;
                case ITA_TYPE:
                    return ITA_TYPE_STRING;
                case TFL_TYPE:
                    return TFL_TYPE_STRING;
                case MR_TYPE:
                    return MR_TYPE_STRING;
                case FOCUS_TYPE:
                    return FOCUS_TYPE_STRING;
                case DCS_TYPE:
                    return DCS_TYPE_STRING;
                case MME_TYPE:
                    return MME_TYPE_STRING;
                case TPS_TYPE:
                    return TPS_TYPE_STRING;
                case BO_TYPE:
                    return BO_TYPE_STRING;
                case MPPI_TYPE:
                    return MPPI_TYPE_STRING;
                case ASAS_TYPE:
                    return ASAS_TYPE_STRING;
                case BJC_TYPE:
                    return BJC_TYPE_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(processType).toString();
            }
        }
        else
        {
            return new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(format).toString();
        }
    }
}
