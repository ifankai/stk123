/**
 * dmLabLib, a Library created for use in various projects at the Data Mining Lab  
 * (http://dmlab.cs.gsu.edu/) of Georgia State University (http://www.gsu.edu/).  
 *  
 * Copyright (C) 2019 Georgia State University
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 3.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package edu.gsu.cs.dmlab.solgrind.base.types.event;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import edu.gsu.cs.dmlab.solgrind.base.EventType;


/**
 * @author Data Mining Lab, Georgia State University
 * 
 */
public class EventSequence implements Comparable<EventSequence>{

    private ArrayList<EventType> sequenceEvents = null;

    private double piValue = Double.MIN_VALUE;

    public EventSequence() {
        sequenceEvents = new ArrayList<>();
    }

    public EventSequence(EventType e) {
        sequenceEvents = new ArrayList<>();
        sequenceEvents.add(e);
    }

    public EventSequence(ArrayList<EventType> eventTypes) {
        sequenceEvents = new ArrayList<>(eventTypes);
    }

    public EventType getType(int index) {
        return sequenceEvents.get(index);
    }

    public String getTypeName(int index) {
        return sequenceEvents.get(index).getType();
    }

    @Override
    public String toString() {
        String listString = sequenceEvents.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) + " " + piValue;
        return listString;
    }

    public EventType getSequenceHead() {
        return sequenceEvents.get(0);
    }

    public static boolean matches(EventSequence s1, EventSequence s2) {
        for (int i = 0; i < s1.getLength() - 1; i++) {
            if (!s1.getEventsList().get(i + 1).equals(s2.getEventsList().get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Always check whether those two matches or not!!!
     * @param fs1
     * @param fs2
     * @return connected sequence from fs1 and fs2
     */

    public static EventSequence connect(EventSequence fs1, EventSequence fs2) {
        //pre-condition always check if it matches or not
        ArrayList<EventType> eventTypeList = new ArrayList<EventType>(fs1.getEventsList());
        eventTypeList.add(fs2.getSequenceTail());
        return new EventSequence(eventTypeList);
    }

    public int getLength() {
        return sequenceEvents.size();
    }

    public EventType getSequenceTail() {
        return sequenceEvents.get(sequenceEvents.size() - 1);
    }

    public ArrayList<EventType> getEventsList() {
        return sequenceEvents;
    }

    public EventSequence appendEventType(EventType type) {
        ArrayList<EventType> newSequenceList = new ArrayList<>(this.getEventsList());
        newSequenceList.add(type);
        return new EventSequence(newSequenceList);
    }

    public void insert(EventType type) {
        this.getEventsList().add(type);
    }

    public boolean isValid() {
        if (sequenceEvents == null) {
            return false;
        } else if (sequenceEvents.size() < 2) {
            return false;
        }
        return true;
    }

    public String getTableName() {
        String tableName = "";
        for (EventType e : sequenceEvents) {
            tableName += "_" + e.getType();
        }
        return tableName;
    }

    public EventSequence getFolloweeSubsequence() {
        ArrayList<EventType> followees = new ArrayList<EventType>();
        for (int i = 1; i < this.sequenceEvents.size(); i++) {
            followees.add(this.sequenceEvents.get(i));
        }
        return new EventSequence(followees);
    }
    
    public EventSequence getTailSubsequence(int k) {
    	if(k > this.getLength()){
    		System.err.println("You cannot get a subsequence that is larger in size");
    		return null;
    	}
        ArrayList<EventType> tail = new ArrayList<EventType>();
        for (int i = this.getLength()-k; i < this.getLength(); i++) {
            tail.add(this.sequenceEvents.get(i));
        }
        return new EventSequence(tail);
    }
    
    public EventSequence getHeadSubsequence(int k) {
    	if(k > this.getLength()){
    		System.err.println("You cannot get a subsequence that is larger in size");
    		return null;
    	}
        ArrayList<EventType> head = new ArrayList<EventType>();
        for (int i = 0; i < k; i++) {
            head.add(this.sequenceEvents.get(i));
        }
        return new EventSequence(head);
    }
    
    

    public EventSequence getFollowerSubsequence() {
        ArrayList<EventType> followers = new ArrayList<EventType>();
        for (int i = 0; i < this.sequenceEvents.size() - 1; i++) {
            followers.add(this.sequenceEvents.get(i));
        }
        return new EventSequence(followers);
    }

    public double getPiValue() {
        return piValue;
    }

    public void setPiValue(double piValue) {
        this.piValue = piValue;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;

        if(!(obj instanceof EventSequence)) {
            return false;
        }

        EventSequence temp = (EventSequence)obj;
        return sequenceEvents.equals(temp.getEventsList());
    }

    @Override
    public int hashCode() {
        int code = 17;
        for(EventType s: sequenceEvents) {
            code = 31 * code + s.hashCode();
        }
        return code ;
    }


    @Override
    public int compareTo(EventSequence o) {
        return this.toString().compareTo(o.toString());
    }

    public static final Comparator<EventSequence> piComparator = new Comparator<EventSequence>() {
        @Override
        public int compare(EventSequence o1, EventSequence o2) {
            return new Double(o1.piValue).compareTo(new Double(o2.piValue));
        }
    };
}
