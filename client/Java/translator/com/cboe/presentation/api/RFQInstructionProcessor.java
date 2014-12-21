package com.cboe.presentation.api;

import java.util.*;
import com.cboe.interfaces.presentation.common.instruction.Instruction;
import com.cboe.interfaces.presentation.common.instruction.InstructionProcessor;
import com.cboe.interfaces.presentation.rfq.RFQ;
import com.cboe.presentation.common.instruction.DefaultInstructionProcessor;
import com.cboe.presentation.common.cache.CacheStateChange;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.domain.util.SessionKeyContainer;

/**
  @author Will McNabb
*/
public class RFQInstructionProcessor implements InstructionProcessor
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    Map nonDuplicateMap = Collections.synchronizedMap(new HashMap());

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    public void addInstructions(Instruction[] instructions)
    {
        for (int i=0; i<instructions.length; i++)
        {
            addInstruction(instructions[i]);
        }
    }

    public void addInstruction(Instruction instruction)
    {
        RFQ rfq = (RFQ) instruction.getTarget();
        // If an instruction is added for an RFQ before other instructions for the same RFQ have
        //    been executed, then the new instuction will overwrite the old.
        // After an instruction is executed, it's removed from the Map to prevent instructions from being
        //    executed more than once.
        this.nonDuplicateMap.put(rfq, instruction);
    }

    public void run()
    {
        try {
            Map sessionKeyMap = new HashMap();
            List addedList;
            List removedList;
            List updatedList;

            //Now iterate the collapsed set and fill the three lists
            for (Iterator it = this.nonDuplicateMap.values().iterator(); it.hasNext(); )
            {
                RFQInstruction instruction = (RFQInstruction) it.next();
                RFQ rfq = (RFQ) instruction.getTarget();

                List[] lists = getModListsForSessionClass(sessionKeyMap, new SessionKeyContainer(rfq.getSessionName(), rfq.getClassKey().intValue()));

                //Build three lists - added, removed, and updated
                addedList = lists[0];
                removedList = lists[1];
                updatedList = lists[2];

                if (instruction.isDeleteInstruction())
                {
                    //Added to fix the "lingering rfq" problem due to the fluctuating time delta.
                    //The final instruction is tagged as a forced deletion for the rfq.
                    rfq.forceDelete();
                    removedList.add(rfq);
                }
                else
                {
                    int previousState = rfq.getState();
                    //Perform the operation and see if the state changes
                    boolean stateHasChanged = instruction.performOperation();
                    int newState = rfq.getState();

                    //If the previous state was "DELETED" and there is a state change,
                    //we send out an "ADDED" event.
                    if ( (previousState == RFQ.DELETED) && (newState != RFQ.DELETED) )
                    {
                        addedList.add(rfq);
                    }
                    //Else if the previous state was not "DELETED" and the new state is "DELETED",
                    //we send out a "REMOVED" event.
                    else if ( (previousState != RFQ.DELETED) && (newState == RFQ.DELETED) )
                    {
                        removedList.add(rfq);
                    }
                    //Otherwise we send out an "UPDATED" event.
                    else if ( newState != RFQ.DELETED )
                    {
                        updatedList.add(rfq);
                    }
                }
                // remove each RFQInstruction from the nonDuplicateMap after it's been processed
                it.remove();
            }

            for (Iterator it = sessionKeyMap.keySet().iterator (); it.hasNext(); )
            {

                SessionKeyContainer sessionKey = (SessionKeyContainer) it.next();

                List[] lists = (List[])sessionKeyMap.get(sessionKey);

                addedList = lists[0];
                removedList = lists[1];
                updatedList = lists[2];

                if (addedList.size() > 0 || removedList.size() > 0 || updatedList.size() >0)
                {
                    CacheStateChange stateChangeObject = new CacheStateChange(addedList.toArray(), removedList.toArray(), updatedList.toArray());

                    ChannelKey channelKey = new ChannelKey(ChannelType.CB_RFQ, sessionKey.getSessionName());
                    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, stateChangeObject);
                    EventChannelAdapterFactory.find().dispatch(event);

                    channelKey = new ChannelKey(ChannelType.CB_RFQ, sessionKey);
                    event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, stateChangeObject);
                    EventChannelAdapterFactory.find().dispatch(event);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected List[] getModListsForSessionClass(Map sessionKeyMap, SessionKeyContainer sessionKey)
    {
        List[] modLists = (List[]) sessionKeyMap.get(sessionKey);
        if (modLists == null)
        {
            modLists = new List[3];
            modLists[0] = new ArrayList();
            modLists[1] = new ArrayList();
            modLists[2] = new ArrayList();

            sessionKeyMap.put(sessionKey, modLists);
        }
        return modLists;
    }
}

