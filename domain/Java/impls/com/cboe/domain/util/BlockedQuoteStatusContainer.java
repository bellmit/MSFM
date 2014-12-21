/**
 * This is a container class to contain all the different quote status report types,
 * included in a blocked quote status call. 
 */
package com.cboe.domain.util;

/**
 * @author Gijo Joseph
 *
 */
public class BlockedQuoteStatusContainer {
	public short[] seqmap; 
	public com.cboe.idl.quote.GroupQuoteFillReportStruct[] fillReports; 
	public com.cboe.idl.quote.GroupQuoteFillReportV3Struct[] fillReportsV3; 
	public com.cboe.idl.quote.GroupQuoteDeleteReportStruct[] deleteReports; 
	public com.cboe.idl.quote.GroupQuoteDeleteReportV2Struct[] deleteReportsV2; 
	public com.cboe.idl.quote.GroupQuoteDeleteReportV3Struct[] deleteReportsV3; 
	public com.cboe.idl.quote.GroupQuoteBustReportStruct[] bustReports; 
	public com.cboe.idl.quote.GroupQuoteBustReportV3Struct[] bustReportsV3; 
	public com.cboe.idl.quote.GroupQuoteStatusUpdateStruct[] statusUpdates;
	
	public BlockedQuoteStatusContainer(short[] seqmap,
			com.cboe.idl.quote.GroupQuoteFillReportStruct[] fillReports,
			com.cboe.idl.quote.GroupQuoteFillReportV3Struct[] fillReportsV3,
			com.cboe.idl.quote.GroupQuoteDeleteReportStruct[] deleteReports,
			com.cboe.idl.quote.GroupQuoteDeleteReportV2Struct[] deleteReportsV2,
			com.cboe.idl.quote.GroupQuoteDeleteReportV3Struct[] deleteReportsV3,
			com.cboe.idl.quote.GroupQuoteBustReportStruct[] bustReports,
			com.cboe.idl.quote.GroupQuoteBustReportV3Struct[] bustReportsV3,
			com.cboe.idl.quote.GroupQuoteStatusUpdateStruct[] statusUpdates
			)
	{
		this.seqmap = seqmap;
		this.fillReports = fillReports;
		this.fillReportsV3 = fillReportsV3;
		this.deleteReports = deleteReports;
		this.deleteReportsV2 = deleteReportsV2;
		this.deleteReportsV3 = deleteReportsV3;
		this.bustReports = bustReports;
		this.bustReportsV3 = bustReportsV3;
		this.statusUpdates = statusUpdates;
	}
}
