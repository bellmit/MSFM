#!/usr/bin/ksh
#
# mkCfix.sh -- builds the property files for a CFIX host
#
# Author: Dmitry Volpyansky
#

post="W_MAIN";
template="production"
fileName=$0;

Usage()
{
    print;
    print $1;
    print;

    print "Usage: (-e and -h are mandatory)
       -h Host                                      (eg mdcas01, sbtmdc02, etc)
       -e Engine [ Engine2 EngineN ]                (eg CF1M01, CF2M01, etc)
       -p Post (W_MAIN|ONE_MAIN)                    (for PCS) Default: '${post}'
       -d Output Root Directory                     Default: './host'
       -t Template to use                           Default: '${template}'
       -a                                           (to create override from 'CFIXTEMPLATE.${template}.always.overlay')
       -n                                           (to create override from 'CFIXTEMPLATE.${template}.never.overlay')
       -s                                           (to generate a single CFIX process handling multiple engines)
       -g                                           (to generate defaults)

Examples:
       ksh $fileName -h mdcas01 -e CF1M01 CF2M01
       ksh $fileName -h mdcas01 -e CF1M01 CF2M01 -p ONE_MAIN
       ksh $fileName -h mdcas01 -e CF1M01 CF2M01 -p W_MAIN -d /tmp
       ksh $fileName -h mdcas01 -e CF1M01 CF2M01 -p ONE_MAIN -d /tmp -a
       ksh $fileName -h mdcas01 -e CF1M01 CF2M01 -p ONE_MAIN -d /tmp -n
";
}

integer singleEngine=0
integer alwaysOverlay=0
integer neverOverlay=0
integer defaults=0

integer i=0
integer j=1
integer fixPort=21536
integer fixPort2
integer fixPort3
integer fixPort4
integer touristPort=7536
typeset -Z2 engineNumber

while getopts ":h:e:p:t:d:ansg" opt
do
   case $opt in
      e) set -A engine ${engine[*]} $OPTARG;                      ## adds OPTARG to the engine array
         shift $(( $OPTIND - 1 ));                                ## shifts so that $1 is the next after OPTARG
         until (( $# == 0 ))                                      ## if no more parameters, then exit loop
         do
             if [[ ${1%%[!-]*} == "-" ]]                          ## break if parameter begins with a '-'
             then
                 break;
             fi
             set -A engine ${engine[*]} $1;                       ## adds $1 to the engine array
             shift;                                               ## shift so that $1 is now what used to be $2
         done
         let OPTIND=1;                                            ## for getopts, set the next index to be 1, since we shifted $1/$2/.. off
         ;;
      h) host=$OPTARG;
         ;;
      p) post=$OPTARG;
         ;;
      t) template=$OPTARG;
         ;;
      d) outdir=$OPTARG;
         ;;
      a) alwaysOverlay=1;
         ;;
      n) neverOverlay=1;
         ;;
      s) singleEngine=1;
         ;;
      g) defaults=1;
         ;;
      \?) Usage;
         return;
         ;;
      *) Usage;
         return;
         ;;
   esac
done

alwaysOverlayFile=CFIXTEMPLATE.${template}.always.overlay
neverOverlayFile=CFIXTEMPLATE.${template}.never.overlay

if [[ 0 == ${singleEngine} && ${#engine[*]} < 1 || ${#engine[*]} > 4 ]]; then "can't create more than 2 engines per single CFIX process (file generation limitation)"; return; fi
if [[ -z ${host} ]];                                                     then Usage "Missing -h parameter"; return; fi
if (( 0 == ${#engine[*]} ));                                             then Usage "Missing -e parameter"; return; fi
if [[ ! -z ${alwaysOverlay} && ! -f ${alwaysOverlayFile} ]];             then Usage "No such file '${alwaysOverlayFile}' specified with the -a parameter"; return; fi
if [[ ! -z ${neverOverlay} && ! -f ${neverOverlayFile} ]];               then Usage "No such file '${neverOverlayFile}' specified with the -n parameter"; return; fi

orig_outdir=${outdir}

if [[ -z ${outdir} ]]; then
    outdir="./${host}";
elif [[ -z ${outdir%$host} ]]; then
    outdir="./${host}";
elif [[ ${outdir} != ${outdir%$host} ]]; then
    outdir="${outdir%$host}/${host}";
else
    outdir=${outdir}/${host}
fi

top_outdir=${outdir}

umask 000;

outdir=${outdir}/config/properties

mkdir -p ${outdir};

chmod 777 ${outdir};

if [[ ! -d ${outdir} ]]; then print "can't create directory ${outdir}"; return; fi

if [[ 0 == ${singleEngine} ]]
then
    until (( i == ${#engine[*]} ))
    do
        engineNumber=$j;

        outfile=cfix${engineNumber}v2${host}

        cat CFIXTEMPLATE.${template}.ini | grep -v "TARGETCOMPID2|FIXPORT2" | sed -e "s/TARGETCOMPID/${engine[$i]}/g;s/FIXPORT/${fixPort}/g;s/TOURISTPORT/${touristPort}/g;s/FILEPREFIX/${outfile}/g" > ${outdir}/${outfile}.ini

       sed -e "s/ENGINE/${j}/g" < CFIXTEMPLATE.${template}.${post}.post > ${outdir}/${outfile}.post

        if [[ 0 == ${defaults} ]]
        then
            cp -p CFIXTEMPLATE.${template}.defaults ${outdir}/${outfile}.defaults
            cp -p CFIXTEMPLATE.${template}.senders  ${outdir}/${outfile}.senders
        else
            cp -p CFIXTEMPLATE.${template}.defaults ${outdir}/cfix.defaults
            cp -p CFIXTEMPLATE.${template}.senders ${outdir}/cfix.senders
        fi

        > ${outdir}/${outfile}.overrides

        if [[ ! -z ${alwaysOverlayFile} ]]
        then
            while read line
            do
                print "post.${line}.cfix.fixSession.overlayPolicy=AlwaysOverlay" >> ${outdir}/${outfile}.overrides;
            done < ${alwaysOverlayFile};
        fi

        if [[ ! -z ${neverOverlayFile} ]]
        then
            while read line
            do
                print "post.${line}.cfix.fixSession.overlayPolicy=NeverOverlay" >> ${outdir}/${outfile}.overrides;
            done < ${neverOverlayFile};
        fi

        if [[ ! -s ${outdir}/${outfile}.overrides ]]
        then
            \rm -f ${outdir}/${outfile}.overrides
        fi

        chmod 666 ${outdir}/*

        (( i           = i           + 1));
        (( j           = j           + 1));
        (( fixPort     = fixPort     + 1));
        (( touristPort = touristPort + 1));
    done
else
        engineNumber=1;

        outfile=cfix${engineNumber}v2${host}

        (( fixPort4 = fixPort + 3));
        (( fixPort3 = fixPort + 2));
        (( fixPort2 = fixPort + 1));

        case ${#engine[*]} in
            1)
                cat CFIXTEMPLATE.${template}.ini | \
                    egrep -v "TARGETCOMPID2|FIXPORT2|TARGETCOMPID3|FIXPORT3|TARGETCOMPID4|FIXPORT4" | \
                    sed -e "s/TARGETCOMPID2/${engine[1]}/g;s/FIXPORT2/${fixPort2}/g" | \
                    sed -e "s/TARGETCOMPID/${engine[0]}/g;s/FIXPORT/${fixPort}/g" | \
                    sed -e "s/TOURISTPORT/${touristPort}/g" \
                    > ${outdir}/${outfile}.ini
                ;;
            2)
                cat CFIXTEMPLATE.${template}.ini | \
                    egrep -v "TARGETCOMPID3|FIXPORT3|TARGETCOMPID4|FIXPORT4" | \
                    sed -e "s/TARGETCOMPID2/${engine[1]}/g;s/FIXPORT2/${fixPort2}/g" | \
                    sed -e "s/TARGETCOMPID/${engine[0]}/g;s/FIXPORT/${fixPort}/g" | \
                    sed -e "s/TOURISTPORT/${touristPort}/g" \
                    > ${outdir}/${outfile}.ini
                ;;
            3)
                cat CFIXTEMPLATE.${template}.ini | \
                    egrep -v "TARGETCOMPID4|FIXPORT4" | \
                    sed -e "s/TARGETCOMPID3/${engine[2]}/g;s/FIXPORT3/${fixPort3}/g" | \
                    sed -e "s/TARGETCOMPID2/${engine[1]}/g;s/FIXPORT2/${fixPort2}/g" | \
                    sed -e "s/TARGETCOMPID/${engine[0]}/g;s/FIXPORT/${fixPort}/g" | \
                    sed -e "s/TOURISTPORT/${touristPort}/g" \
                    > ${outdir}/${outfile}.ini
                ;;
            4)
                cat CFIXTEMPLATE.${template}.ini | \
                    sed -e "s/TARGETCOMPID4/${engine[3]}/g;s/FIXPORT4/${fixPort4}/g" | \
                    sed -e "s/TARGETCOMPID3/${engine[2]}/g;s/FIXPORT3/${fixPort3}/g" | \
                    sed -e "s/TARGETCOMPID2/${engine[1]}/g;s/FIXPORT2/${fixPort2}/g" | \
                    sed -e "s/TARGETCOMPID/${engine[0]}/g;s/FIXPORT/${fixPort}/g" | \
                    sed -e "s/TOURISTPORT/${touristPort}/g" \
                    > ${outdir}/${outfile}.ini
                ;;
        esac

        # set ENGINE number to the host nubmer, apimd1 - 01, apimd2 - 02, etc
        ENGHOST=$j
        [[ $host == "apimd1" || $host == "apimd2" || $host == "apimd3" ]] &&  ENGHOST=$(echo $host |sed 's/[a-z]*//g')
        sed -e "s/ENGINE/${ENGHOST}/g" < CFIXTEMPLATE.${template}.${post}.post > ${outdir}/${outfile}.post

        if [[ 0 == ${defaults} ]]
        then
            cp -p CFIXTEMPLATE.${template}.defaults ${outdir}/${outfile}.defaults
            cp -p CFIXTEMPLATE.${template}.senders ${outdir}/${outfile}.senders
        else
            cp -p CFIXTEMPLATE.${template}.defaults ${outdir}/cfix.defaults
            cp -p CFIXTEMPLATE.${template}.senders ${outdir}/cfix.senders
        fi

        > ${outdir}/${outfile}.overrides

        if [[ ! -z ${alwaysOverlayFile} ]]
        then
            while read line
            do
                print "post.${line}.cfix.fixSession.overlayPolicy=AlwaysOverlay" >> ${outdir}/${outfile}.overrides;
            done < ${alwaysOverlayFile};
        fi

        if [[ ! -z ${neverOverlayFile} ]]
        then
            while read line
            do
                print "post.${line}.cfix.fixSession.overlayPolicy=NeverOverlay" >> ${outdir}/${outfile}.overrides;
            done < ${neverOverlayFile};
        fi

        if [[ ! -s ${outdir}/${outfile}.overrides ]]
        then
            \rm -f ${outdir}/${outfile}.overrides
        fi

        (( touristPort = touristPort + 1));
fi

tar fc ${orig_outdir}/cfix_${host}.tar -C ${top_outdir} config && \rm -rf ${top_outdir} && print "Created ${orig_outdir}/cfix_${host}.tar"
