package com.cboe.application.systemHealth;

import com.cboe.domain.instrumentorExtension.*;
import com.cboe.client.xml.XmlBindingFacade;

/**
 * @author Jing Chen
 */
public class TestInstrumentorContextDetail
{
    public static void initTest()
    {
        try
        {
            ThreadPoolInstrumentorExtension tpExtension = ThreadPoolInstrumentorExtensionFactory.createThreadPoolInstrumentor("JIM/chenjnt:8003/UserSessionMarketDataThreadPool1234",9,null,true);
            QueueInstrumentorExtension qiExtension = QueueInstrumentorExtensionFactory.createQueueInstrumentor("JIM/chenjnt:8003/CM/ConsumerProxy2343", null, tpExtension, true);
            QueueInstrumentorExtensionFactory.createQueueInstrumentor("JIM/chenjnt:8003/BD/ConsumerProxy3433", null, tpExtension, true);
            MethodInstrumentorExtension miExtension = MethodInstrumentorExtensionFactory.createMethodInstrumentor("JIM/chenjnt:8003/CM/ConsumerProxy2343/acceptCurrentMarket", null, qiExtension, true);
            String request = XmlBindingFacade.getInstance().createContextDetailRequest("testContextDetail", new String[]{"JIM/chenjnt:8003/CM/ConsumerProxy2343","JIM/chenjnt:8003/",
                                                                                                        "JIM/chenjnt:8003/UserSessionMarketDataThreadPool1234"});
            ContextDetailQueryProcessorImpl.initialize();
            ContextDetailQueryProcessorImpl processor = new ContextDetailQueryProcessorImpl(request);
            System.out.println(processor.processRequest());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        initTest();
    }
}
