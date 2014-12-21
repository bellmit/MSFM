/*****************************************************************************
Copyright (c) 2000-2001 Javelin Technologies, Inc
All rights reserved.

File:        apping.c
Description: Ping program using ICMP and RAW Sockets
Author:      Benedict Zoe

    This program is a socket driven ping client. It takes one argument (port)
    or two arguments (host, port) to establish a server socket connection.

    After connecting to the server, it continually expects two lines
    (new line terminated), the first being the host to ping and the second
    a response value.  It pings the requested host once, then writes the host
    and response values back to the server.

    This program will hang if the pinged host does not respond.

    This program will exit(4) on any error during the ping stage.

COMPILE WINDOWS:
    cl apping.c /link wsock32.lib

COMPILE UNIX:
    cc -o apping apping.c -lsocket -lnsl

    su root
    chown root apping
    chmod +s apping

Notes:
    Compile with -DTESTING for output during testing.

HISTORY:
    12/12/01 bzoe   Terminate on socket read/recv == 0.
	11/13/01 bzoe	Added steps on how to set root owner and setuid bit.
    11/02/01 bzoe   Created.
*****************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#   include <winsock.h>
#else
#   include <errno.h>
#   include <netdb.h>
#   include <unistd.h>
#   include <sys/ioctl.h>
#   include <sys/time.h>
#   include <sys/types.h>
#   include <sys/socket.h>
#   define closesocket            close
#   define ioctlsocket            ioctl
#   define SOCKET_ERROR           (-1)

    /* Handy defines that Microsoft provides */
    typedef struct sockaddr_in  SOCKADDR_IN;
    typedef struct hostent*     PHOSTENT;
    typedef struct sockaddr*    PSOCKADDR;
    typedef struct servent*     PSERVENT;
    typedef int                 SOCKET;
    typedef struct timeval      TIMEVAL;
#endif

#ifdef TESTING
#   define  recvsocket(fd,ps,cn)   read(fd,ps,cn)
#   define  sendsocket(fd,ps,cn)   write(fd,ps,cn)
#else
#   define  recvsocket(fd,ps,cn)   recv(fd,ps,cn,0)
#   define  sendsocket(fd,ps,cn)   send(fd,ps,cn,0)
#endif

/* ------------------------------------------------------------------------ */
/* ICMP Structures                                                          */
/* ------------------------------------------------------------------------ */
#pragma pack(1)

#define ICMP_ECHOREPLY  (0)     /*  The response                            */
#define ICMP_ECHO       (8)     /*  The request                             */

/* IP Header -- RFC 791                                                     */
typedef struct tagIP_HEADER
{
    u_char  ip_hl:4;        /* header length                                */
    u_char  ip_v:4;         /* version.                                     */
    u_char  ip_tos;         /* Type Of Service                              */
    short   ip_len;         /* Total Length                                 */
    short   ip_id;          /* Identification                               */
    short   ip_off;         /* Flags and Fragment Offset                    */
    u_char  ip_ttl;         /* Time To Live                                 */
    u_char  ip_p;           /* Protocol                                     */
    u_short ip_sum;         /* Checksum                                     */
    struct  in_addr ip_src; /* Internet Address - Source                    */
    struct  in_addr ip_dst; /* Internet Address - Destination               */
} IP_HEADER;


/* ICMP Header - RFC 792                                                    */
typedef struct tagICMP_HEADER
{
    u_char  icmp_type;      /* Type                                         */
    u_char  icmp_code;      /* Code                                         */
    u_short icmp_cksum;     /* Checksum                                     */
    u_short icmp_id;        /* Identification                               */
    u_short icmp_seq;       /* Sequence                                     */
    char    icmp_data[1];   /* Data starts here                             */
} ICMP_HEADER;

#define REQ_DATASIZE (32)   /* Echo Request Data size                       */

/* ICMP Echo Request                                                        */
typedef struct tagECHOREQUEST
{
    ICMP_HEADER icmp_header;
    char        chData[REQ_DATASIZE - 1];    /* Less icmp_data[1]           */
} ECHOREQUEST;


/* ICMP Echo Reply                                                          */
typedef struct tagECHOREPLY
{
    IP_HEADER   ip_header;
    ECHOREQUEST request;
} ECHOREPLY;

#pragma pack()

/* ------------------------------------------------------------------------ */
/* Definitions.                                                             */
/* ------------------------------------------------------------------------ */
#define MAX_COMMAND (256)

/* ------------------------------------------------------------------------ */
/* Globals                                                                  */
/* ------------------------------------------------------------------------ */
u_short gPid;

/* ------------------------------------------------------------------------ */
/* Internal Functions                                                       */
/* ------------------------------------------------------------------------ */
void  command(SOCKET fdIn,SOCKET fdOut);
char* Fgets(char* psGets,int cnGets,SOCKET fd);
void  Fputs(const char* psPuts,SOCKET fd);
char* trims(char* psDst,const char* psSrc);
void  ping(const char* pstrHost,int cnCount);
void  send_request(SOCKET,SOCKADDR_IN*);
void  recv_reply(SOCKET,SOCKADDR_IN*,TIMEVAL*,u_char*);
void  err_quit(const char* pstrFrom);
u_short in_cksum(u_short *addr, int len);
void tv_sub(TIMEVAL* out,const TIMEVAL* in);

/* ------------------------------------------------------------------------ */
/* Implementation.                                                          */
/* ------------------------------------------------------------------------ */
int main(int argc,const char* argv[])
{
    int     fd;
#ifdef _WIN32
    WSADATA wsaData;
    WORD    wVersionRequested = MAKEWORD(1,1);
#endif

    if (argc <=1 )
    {
        fprintf(stderr,"\nSyntax: %s port",argv[0]);
        fprintf(stderr,"\nSyntax: %s host port\n",argv[0]);
        exit(1);
    }

#ifdef _WIN32
    /* Init WinSock */
    if (0 != WSAStartup(wVersionRequested, &wsaData))
    {
        fprintf(stderr,"\nError initializing WinSock\n");
        exit(2);
    }

    /* Check version    */
    if (wsaData.wVersion != wVersionRequested)
    {
        fprintf(stderr,"\nWinSock version not supported\n");
        exit(3);
    }
#endif

    /* Identify. */
    gPid = (u_short)getpid();

    if (argc <= 2)
    {
        fd = tcp_connect("localhost",argv[1]);
        command(fd,fd);
    }
    else if (argc <= 3)
    {
        fd = tcp_connect(argv[1],argv[2]);
        command(fd,fd);
    }
    else /* testing */
    {
        command(0,1);
    }
}

/**
 * The command loop.
 * @param fdIn input socket.
 * @param fdOut output socket.
 */
void command(SOCKET fdIn,SOCKET fdOut)
{
    char    szHostname[MAX_COMMAND];
    char    szHostTrim[MAX_COMMAND];
    char    szResponse[MAX_COMMAND];

    for (;;)
    {
        Fgets(szHostname,MAX_COMMAND,fdIn);
        Fgets(szResponse,MAX_COMMAND,fdIn);

        ping(trims(szHostTrim,szHostname),1);

        Fputs(szHostname,fdOut);
        Fputs(szResponse,fdOut);
    }
}

/**
 * Perform TCP connect.
 * @param psHost to connect to.
 * @param psPort to connect to.
 * @returns the socket.
 */
int tcp_connect(const char* psHost,const char* psPort)
{
    SOCKET      fd;
    u_short     iPort;
    PHOSTENT    pHostent;
    PSERVENT    pServent;
    SOCKADDR_IN sin;

    if (NULL != (pServent = getservbyname(psPort,"tcp")))
    {
        iPort = ntohs(pServent->s_port);
    }
    else
    {
        iPort = (u_short)atoi(psPort);
    }
    if (iPort < 0)
    {
        err_quit("getservbyname()");
    }

    if (SOCKET_ERROR == (fd = socket(AF_INET,SOCK_STREAM,0)))
    {
        err_quit("socket()");
    }
    if (NULL == (pHostent = gethostbyname(psHost)))
    {
        err_quit("gethostbyname()");
    }

    memset(&sin,0,sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_port   = htons(iPort);

    memcpy(&sin.sin_addr,pHostent->h_addr,pHostent->h_length);

    if (0 != connect(fd,(PSOCKADDR)&sin,sizeof(sin)))
    {
        err_quit("connect()");
    }
    return fd;
}

/**
 * Emulate the fgets() behavior but for only one input socket.
 * @param psGets buffer data here.
 * @param cnGets buffer size.
 * @param fd socket handle.
 * @returns the input buffer.
 */
char* Fgets(char* psGets,int cnGets,SOCKET fd)
{
    static int  cnBuffer = 0;       /* Amount buffered                  */
    static char szBuffer[1024];     /* Only supports 1 socket stream!   */

    int   cnRead;
    char* psEnd;
    int   readTries = 5;

    do
    {
        szBuffer[cnBuffer] = '\0';  /* stringize    */
        psEnd = strchr(szBuffer,'\n');

        if (psEnd)
        {
            psEnd += 1;
            cnRead = psEnd - szBuffer;
            if (cnRead >= cnGets) cnRead = cnGets - 1;
            strncpy(psGets,szBuffer,cnRead);
            psGets[cnRead] = '\0';  /* stringize    */

            cnBuffer -= cnRead;
            memmove(szBuffer,psEnd,cnBuffer);
            return psGets;
        }

        cnRead = recvsocket(fd,szBuffer+cnBuffer,sizeof(szBuffer)-cnBuffer);

        if (cnRead <= 0)
        {
          --readTries;
        }
        else
        {
            cnBuffer += cnRead;
            readTries = 5;
        }
    }
    while (readTries);

    err_quit("recv() closed.");
}

/**
 * Emulate the Fputs() behavior.
 * @param psPuts this data.
 * @param fd to this socket.
 */
void Fputs(const char* psPuts,SOCKET fd)
{
    int cnPuts = strlen(psPuts);
    int cnSent;

    while (cnPuts)
    {
        cnSent = sendsocket(fd,psPuts,cnPuts);
        if (cnSent < 0) err_quit("send()");
        psPuts += cnSent;
        cnPuts -= cnSent;
    }
}


#ifdef _WIN32
/**
 * Emulate UNIX gettimeofday()
 */
int gettimeofday(TIMEVAL* ptv,void* avoid)
{
    SYSTEMTIME  st;

    GetSystemTime(&st);
    ptv->tv_sec  = st.wSecond;
    ptv->tv_usec = st.wMilliseconds * 1000;
    return 0;
}
#endif


/**
 * Copy the string and trim trailing newlines.
 * @param psDst destination.
 * @param psSrc Source.
 * @returns destination.
 */
char* trims(char* psDst,const char* psSrc)
{
    strcpy(psDst,psSrc);
    psDst[strcspn(psDst,"\r\n")] = '\0';
    return psDst;
}


/**
 * Perform the ping.
 * @param psHost to ping.
 * @param cnCount number of pings to send.
 */
void ping(const char* psHost,int cnCount)
{
    SOCKET       fd;
    PHOSTENT     pHost;
    SOCKADDR_IN  saDst;
    SOCKADDR_IN  saSrc;
    TIMEVAL      tvSend;
    TIMEVAL      tvRecv;
    int          nLoop;
    u_char       ucTTL;

    /* Create a Raw socket  */
    fd = socket(AF_INET,SOCK_RAW,IPPROTO_ICMP);
    if (fd == SOCKET_ERROR) err_quit("ping: socket()");

    /* Lookup host  */
    pHost = gethostbyname(psHost);
    if (pHost == NULL) err_quit("ping: gethostbyname()");

    /* Setup destination socket address */
    saDst.sin_addr.s_addr = *((u_long*)(pHost->h_addr));
    saDst.sin_family = AF_INET;
    saDst.sin_port = 0;

    /* Tell the user what we're doing   */
#   ifdef TESTING
    printf("\nPinging %s [%s] with %d bytes of data:\n"
                ,psHost
                ,inet_ntoa(saDst.sin_addr)
                ,REQ_DATASIZE);
#   endif

    /* Ping multiple times  */
    for (nLoop = 0; nLoop < cnCount; nLoop++)
    {
        /* Send ICMP echo request   */
        send_request(fd,&saDst);

        /* Receive reply    */
        recv_reply(fd,&saSrc,&tvSend,&ucTTL);

        /* Calculate elapsed time   */
        gettimeofday(&tvRecv,NULL);
        tv_sub(&tvRecv,&tvSend);

#       ifdef TESTING
        printf("\nReply from: %s: bytes=%d time=%ldms TTL=%d"
               ,inet_ntoa(saSrc.sin_addr)
               ,REQ_DATASIZE
               ,tvRecv.tv_sec * 1000 + (tvRecv.tv_usec/1000)
               ,ucTTL);
#       endif
    }
#   ifdef TESTING
    printf("\n");
#   endif

    if (0 != closesocket(fd))
    {
        err_quit("ping: closesocket()");
    }
}


/**
 * Send the ICMP ping packet.
 *
 * Fill in echo request header and send to destination
 * @param fd socket descriptor.
 * @param psaDst to this address.
 */
void send_request(SOCKET fd,SOCKADDR_IN* psaDst)
{
    static ECHOREQUEST  request;
    static u_short      usSeq = 0;

    int     i;
    TIMEVAL tv;
    int     rc;

    /* Fill in echo request */
    request.icmp_header.icmp_type  = ICMP_ECHO;
    request.icmp_header.icmp_code  = 0;
    request.icmp_header.icmp_cksum = 0;
    request.icmp_header.icmp_id    = gPid;
    request.icmp_header.icmp_seq   = ++usSeq;

    /* Fill in some data to send */
    for (i = 0; i < (REQ_DATASIZE-1); i++)
    {
        request.chData[i] = (i + usSeq) % 256;
    }

    /* Set the sending time. */
    gettimeofday(&tv,NULL);
    memcpy(request.icmp_header.icmp_data,&tv,sizeof(TIMEVAL));

    /* Put data in packet and compute checksum */
    request.icmp_header.icmp_cksum =
                        in_cksum((u_short*)&request,sizeof(ECHOREQUEST));

    /* Send the echo request */
    rc = sendto(fd                      /* socket                       */
               ,(char*)&request         /* buffer                       */
               ,sizeof(ECHOREQUEST)     /* sizeof buffer                */
               ,0                       /* flags                        */
               ,(PSOCKADDR)psaDst       /* destination                  */
               ,sizeof(SOCKADDR_IN));   /* address length               */

    if (rc == SOCKET_ERROR) err_quit("ping: sendto()");
}


/**
 * Wait for the echo response.
 * @param fd socket descriptor.
 * @param psaSrc socket address.
 * @param pTTL time to live response.
 * @returns the time sent.
 */
void recv_reply(SOCKET fd,SOCKADDR_IN* psaSrc,TIMEVAL* ptv,u_char* pTTL)
{
    int       cnAddress = sizeof(struct sockaddr_in);
    ECHOREPLY reply;
    int       rc;
    u_short   uPid;

    for (;;)
    {
        /* Receive the echo reply                                       */
        rc = recvfrom(fd                    /* socket                   */
                     ,(char*)&reply         /* buffer                   */
                     ,sizeof(ECHOREPLY)     /* size of buffer           */
                     ,0                     /* flags                    */
                     ,(PSOCKADDR)psaSrc     /* From address             */
                     ,&cnAddress);          /* pointer to address len   */

        /* Check return value */
        if (rc == SOCKET_ERROR) err_quit("ping: recvfrom()");

        /* Too little? */
        if (rc < sizeof(ECHOREPLY))
        {
#           if TESTING
            fprintf(stderr,"\nping: too little data");
#           endif
            continue;
        }

        /* Wait for our packet with our pid */
        memcpy(&uPid,&reply.request.icmp_header.icmp_id,sizeof(uPid));
        if (uPid != gPid)
        {
#           if TESTING
            fprintf(stderr,"\nping: reply wrong pid");
#           endif
            continue;
        }

        /* return time sent and IP TTL */
        *pTTL = reply.ip_header.ip_ttl;
        memcpy(ptv,reply.request.icmp_header.icmp_data,sizeof(TIMEVAL));
        return;
    }
}

/**
 * What happened and exit.
 * @param psReason error occured.
 */
void err_quit(const char* psReason)
{
#ifdef _WIN32
    fprintf(stderr,"\n%s error: %d\n",psReason,WSAGetLastError());
    WSACleanup();
#else
    fprintf(stderr,"\n%s error: %d, %s\n",psReason,errno,strerror(errno));
#endif
    fflush(stderr);
    exit(4);
}

/**
 * Mike Muuss' in_cksum() function and
 * his comments from the original ping program.
 *
 * Author -
 *  Mike Muuss
 *  U. S. Army Ballistic Research Laboratory
 *  December, 1983
 *
 *          I N _ C K S U M
 *
 * Checksum routine for Internet Protocol family headers (C Version)
 *
 * @param addr pointer to data (assumes 2-byte u_short).
 * @param len of data in bytes.
 *
 */
u_short in_cksum(u_short *addr, int len)
{
    register int        nleft = len;
    register u_short    *w = addr;
    register u_short    answer;
    register int        sum = 0;

    /*
     *  Our algorithm is simple, using a 32 bit accumulator (sum),
     *  we add sequential 16 bit words to it, and at the end, fold
     *  back all the carry bits from the top 16 bits into the lower
     *  16 bits.
     */
    while( nleft > 1 ) {
        sum += *w++;
        nleft -= 2;
    }

    /* mop up an odd byte, if necessary */
    if( nleft == 1 ) {
        u_short u = 0;

        *(u_char *)(&u) = *(u_char *)w ;
        sum += u;
    }

    /*
     * add back carry outs from top 16 bits to low 16 bits
     */
    sum = (sum >> 16) + (sum & 0xffff); /* add hi 16 to low 16 */
    sum += (sum >> 16);         /* add carry */
    answer = ~sum;              /* truncate to 16 bits */
    return (answer);
}


/**
 * Difference in timeval: out -= in.
 * @param out pointer and result.
 * @param in pointer.
 */
void tv_sub(TIMEVAL* out,const TIMEVAL* in)
{
    if ( (out->tv_usec -= in->tv_usec) < 0) {   /* out -= in */
        out->tv_sec  -= 1;
        out->tv_usec += 1000000;
    }
    out->tv_sec -= in->tv_sec;
}
