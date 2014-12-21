BEGIN {
	print  
	print "           The following is the list of Servers exceeding the User Threshold"	
	print "Threshold for CAS = 10,MDCAS = 300,MDX = 300,FIX(1,2,3)a/b Engine1-5=25,Other Fix=10, CFIX=100"
	print "=============================================================================================="
}
/^cas/	{if ($2 >=0) 
		if ( $2 >= 10 ) 
			printf( "Server: %s User Count===> %s \n",$1,$2)
		}

/^mdcas/ {if ($2 >=0) 
		if ( $2 >= 300 ) 
			printf( "Server: %s User Count===> %s \n",$1,$2)
		}

/^prdmdx/ {if ($2 >=0) 
		if ( $2 >= 300 ) 
			printf( "Server: %s User Count===> %s \n",$1,$2)
		}
/^fix[1-3][a|b]\.0[1-5]/ { if ($2 >=0)
			if ( $2 >=25 )
			printf( "Server: %s User Count===> %s \n",$1,$2)
		     }
 /^fix/ && ! /^fix[1-3][a|b]\.0[1-5]/ {if ($2 >=0) 
					if ( $2 >= 10 ) 
					printf( "Server: %s User Count===> %s \n",$1,$2)
				  }

/^prdcfix/ {if ($2 >=0) 
		if ( $2 >= 100 ) 
			printf( "Server: %s User Count===> %s \n",$1,$2)
		}
END {
	print
	print "End of User Threshold Report"
	print "============================"
	print
}
