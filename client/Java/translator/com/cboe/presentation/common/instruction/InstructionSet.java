package com.cboe.presentation.common.instruction;

import java.util.*;
import javax.swing.event.*;
import com.cboe.interfaces.presentation.common.instruction.Instruction;
import com.cboe.interfaces.presentation.common.instruction.InstructionIdentifier;
import com.cboe.interfaces.presentation.common.instruction.InstructionSetListener;

/**
   InstructionSet is a collection used to manage instructions to a receiver.
   @author Will McNabb
 */
public class InstructionSet
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    protected List queue;
    protected List identifiers;
    protected EventListenerList listenerList;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    public InstructionSet()
    {
        this.identifiers = new ArrayList();
        this.queue = new ArrayList();
        this.listenerList = new EventListenerList();
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
     Puts the given instruction into the set.
     @param Instruction instruction
     */
    public synchronized void put(Instruction instruction)
    {
        InstructionIdentifier id = instruction.getIdentifier();
        if (!this.identifiers.contains(id))
        {
            internalAdd(instruction);
        }
    }

    /**
     Gets the next available instruction and removes it from the set
     @throws NoSuchElementException
     @return Instruction
     */
    public synchronized Instruction getNext() throws NoSuchElementException
    {
        Instruction retVal = peekNext();
        internalRemove(0);
        return retVal;
    }

    /**
     Gets the instruction with the given ID and removes it from the set.
     @throws NoSuchElementException
     @param InstructionIdentifier instructionID
     @return Instruction
     */
    public synchronized Instruction get(InstructionIdentifier instructionID) throws NoSuchElementException
    {
        Instruction retVal = peek(instructionID);
        internalRemove(indexOf(instructionID));
        return retVal;
    }

    /**
     Gets the instructions that lie between the given indexes within the set and removes them from the set.
     @throws IndexOutOfBoundsException
     @return Instruction[]
     */
    public synchronized Instruction[] get(int index0, int index1) throws IndexOutOfBoundsException
    {
        Instruction[] retVal = new Instruction[index1 - index0];
        int index = 0;
        for (int i=index0; i<=index1; i++)
        {
            retVal[index++] = (Instruction)this.queue.get(i);
            internalRemove(i);
        }
        return retVal;
    }

    /**
     Peeks the next instruction without removing it from the set.
     @throws NoSuchElementException
     @return Instruction
     */
    public synchronized Instruction peekNext() throws NoSuchElementException
    {
        Instruction retVal = null;
        if (this.queue.size() == 0)
        {
            throw new NoSuchElementException("There are no more items in the instruction set");
        }
        else
        {
            retVal = (Instruction) this.queue.get(0);
        }
        return retVal;
    }

    /**
     Peeks the instruction with the given ID without removing it from the set.
     @throws NoSuchElementException
     @param InstructionIdentifier instructionID
     @return Instruction
     */
    public synchronized Instruction peek(InstructionIdentifier instructionID) throws NoSuchElementException
    {
        Instruction retVal = null;
        int instructionIndex = this.identifiers.indexOf(instructionID);
        if ( (this.queue.size() == 0) || instructionIndex == -1 )
        {
            throw new NoSuchElementException("No Instruction exists with identifier " + instructionID);
        }
        else
        {
            retVal = (Instruction) this.queue.get(instructionIndex);
        }
        return retVal;
    }

    /**
     Removes the instruction with the given ID from the set.
     @param Object instructionID
     @return void
     */
    public synchronized void remove(Object instructionID) throws NoSuchElementException
    {
        int instructionIndex = this.identifiers.indexOf(instructionID);
        if ( (this.queue.size() == 0) || instructionIndex == -1 )
        {
            throw new NoSuchElementException("No Instruction exists with identifier " + instructionID);
        }
        else
        {
            internalRemove(instructionIndex);
        }
    }

    /**
     Returns the index of the instruction with the given id. If the instruction does not exist in the set, -1 is returned.
     @param Object instructionID
     @return int
     */
    public synchronized int indexOf(Object instructionID)
    {
        return this.identifiers.indexOf(instructionID);
    }

    /**
     Returns the size of this InstructionSet.
     @return int
     */
    public int size()
    {
        return this.queue.size();
    }

    /**
     Adds an InstructionSetListener to this set.
     @param InstructionSetListener lis
     */
    public void addInstructionSetListener(InstructionSetListener lis)
    {
        this.listenerList.add(InstructionSetListener.class, lis);
    }

    /**
     Removes an InstructionSetListener from this set.
     @param InstructionSetListener lis
    */
    public void removeInstructionSetListener(InstructionSetListener lis)
    {
        this.listenerList.remove(InstructionSetListener.class, lis);
    }

///////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS

    /**
     Fires the event indicating that instructions are currently available
     in the set.
     */
    protected void fireInstructionsAvailable()
    {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2)
        {
          if (listeners[i] == InstructionSetListener.class)
          {
                ((InstructionSetListener)listeners[i+1]).instructionsAvailable();
          }
        }
    }

    /**
     Fires the event indicating that the set is now empty.
     */
    protected void fireSetEmpty()
    {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2)
        {
          if (listeners[i] == InstructionSetListener.class)
          {
                ((InstructionSetListener)listeners[i+1]).setIsEmpty();
          }
        }
    }

    /**
      Non-sychronized removal for internal use only.
      @param int index
    */
    protected void internalRemove(int index)
    {
        //Remove the element and its identifier.
        this.queue.remove(index);
        this.identifiers.remove(index);

        //Alert listeners that the set is empty
        if (this.queue.size() == 0)
        {
            fireSetEmpty();
        }
    }

    /**
      Non-sychronized addition for internal use only.
      @param int index
    */
    protected void internalAdd(Instruction instruction)
    {
        boolean isFirst = (this.queue.size() == 0);
        //Add the element and its identifier.
        this.queue.add(instruction);
        this.identifiers.add(instruction.getIdentifier());
        //Alert listeners that the set now has an item
        if (isFirst)
        {
            fireInstructionsAvailable();
        }
    }

    /**
      Non-sychronized addition for internal use only.
      @param int index
    */
    protected void internalAdd(Instruction[] instructions)
    {
        boolean isFirst = (this.queue.size() == 0);
        for (int i=0; i<instructions.length; i++)
        {
            //Add the element and its identifier.
            this.queue.add(instructions[i]);
            this.identifiers.add(instructions[i].getIdentifier());
        }

        //Alert listeners that the set now has items
        if (isFirst)
        {
            fireInstructionsAvailable();
        }
    }
}

