package com.cboe.presentation.common.instruction;

import java.util.*;
import com.cboe.interfaces.presentation.common.instruction.Instruction;

/**
   An InstructionSet implementation that uses a Comparator to enforce a sorting order.
   @author Will McNabb
 */
public class SortedInstructionSet extends InstructionSet
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    protected SortedSet sortedInstructions;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
       SortedInstructionSet constructor
     */
    public SortedInstructionSet()
    {
        super();
        this.sortedInstructions = new TreeSet();
    }
    /**
       SortedInstructionSet constructor
       @param Comparator comparator
     */
    public SortedInstructionSet(Comparator comparator)
    {
        super();
        this.sortedInstructions = new TreeSet(comparator);
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
      Returns the comparator used to define the sorted order of this set's
      instructions (if a comparator override exists).
      @return Comparator
    */
    public Comparator getComparator()
    {
        return this.sortedInstructions.comparator();
    }

    /**
      Gets the Instructions that are greater than or equal to the given
      instruction when compared.
      @param Instruction instruction
      @return SortedSet
    */
    public synchronized Instruction[] getInstructionsGreaterThanOrEqual(Instruction instruction)
    {
        SortedSet instructions = this.sortedInstructions.tailSet(instruction);
        Instruction[] instArray = (Instruction[])instructions.toArray(new Instruction[instructions.size()]);
        for(int i=0; i<instArray.length; i++)
        {
            Instruction inst = (Instruction)instArray[i];
            internalRemove(queue.indexOf(inst));
        }
        return instArray;
    }
    /**
      Gets the Instructions that are less than or equal to the given
      instruction when compared.
      @param Instruction instruction
      @return SortedSet
    */
    public synchronized Instruction[] getInstructionsLessThanOrEqual(Instruction instruction)
    {
        SortedSet instructions = this.sortedInstructions.headSet(instruction);
        Instruction[] instArray = (Instruction[])instructions.toArray(new Instruction[instructions.size()]);
        for(int i=0; i<instArray.length; i++)
        {
            Instruction inst = (Instruction)instArray[i];
            internalRemove(queue.indexOf(inst));
        }
        return instArray;
    }

///////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS

    /**
      Non-sychronized removal for internal use only.
      @param int index
    */
    protected void internalRemove(int index) throws IndexOutOfBoundsException
    {
        Instruction instruction = (Instruction)this.queue.get(index);
        if (instruction != null)
        {
            this.sortedInstructions.remove(instruction);
        }
        super.internalRemove(index);
    }

    /**
      Non-sychronized addition for internal use only.
      @param int index
    */
    protected void internalAdd(Instruction instruction)
    {
        boolean isFirst = (this.queue.size() == 0);
        int insertionIndex = getInsertionIndex(instruction);

        this.sortedInstructions.add(instruction);

        if (insertionIndex == -1)
        {
            this.queue.add(instruction);
            this.identifiers.add(instruction.getIdentifier());
        }
        else
        {
            this.queue.add(insertionIndex, instruction);
            this.identifiers.add(insertionIndex, instruction.getIdentifier());
        }

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
            int insertionIndex = getInsertionIndex(instructions[i]);

            this.sortedInstructions.add(instructions[i]);

            if (insertionIndex == -1)
            {
                this.queue.add(instructions[i]);
                this.identifiers.add(instructions[i].getIdentifier());
            }
            else
            {
                this.queue.add(insertionIndex, instructions[i]);
                this.identifiers.add(insertionIndex, instructions[i].getIdentifier());
            }
        }
        //Alert listeners that the set now has items
        if (isFirst)
        {
            fireInstructionsAvailable();
        }
    }

    /**
      Gets the insertion index for the given instruction. Returns -1 if
      no valid insertion index is found.
      @param Instruction instruction
      @return int
    */
    protected int getInsertionIndex(Instruction instruction)
    {
        int insertionIndex = -1;
        //See if there are any identfiers that are greater than or equal to the given one.
        SortedSet tailSet = this.sortedInstructions.tailSet(instruction);
        if (tailSet.size() > 0)
        {
            Instruction insertionInstruction = (Instruction)tailSet.first();
            insertionIndex = this.identifiers.indexOf(insertionInstruction);
        }
        return insertionIndex;
    }
}
