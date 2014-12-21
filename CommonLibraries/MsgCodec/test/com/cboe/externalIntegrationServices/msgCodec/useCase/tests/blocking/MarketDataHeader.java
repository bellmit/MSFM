package com.cboe.externalIntegrationServices.msgCodec.useCase.tests.blocking;

import com.cboe.externalIntegrationServices.msgCodec.ICodec;
import com.cboe.externalIntegrationServices.msgCodec.IntFieldCodec;
import com.cboe.externalIntegrationServices.msgCodec.MsgCodec;
import com.cboe.externalIntegrationServices.msgCodec.useCase.CodecConstants;

public class MarketDataHeader extends MsgCodec implements CodecConstants {

    final private IntFieldCodec groupKey;
    final private IntFieldCodec subIdentifier;

    public MarketDataHeader(String p_name) {
        super(p_name, MarketDataHeaderID);
        
        groupKey = new IntFieldCodec("groupKey");
        add(groupKey);
        
        subIdentifier = new IntFieldCodec("subIdentifier");
        add(subIdentifier);
    }

    public ICodec newCopy() {
        return new MarketDataHeader(getName());
    }

    public void setGroupKey(int groupKey) {
        this.groupKey.setValue(groupKey);
    }

    public int getGroupKey() {
        return groupKey.getValue();
    }
    public void setSubIdentifier(int subIdentifier) {
        this.subIdentifier.setValue(subIdentifier);
    }

    public int getSubIdentifier() {
        return subIdentifier.getValue();
    }
}