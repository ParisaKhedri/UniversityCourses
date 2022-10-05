package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.ArrayList;
import java.util.Random;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

//Node class for making node-objects representing squares
class Node{
	public int x;
	public int y;
}


class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 1000;
	public MyAgentState state = new MyAgentState();
	
	public boolean found_first_bump = false;   
	public boolean found_second_bump = false;
	public boolean found_bump_going_home = false;
	public boolean finished_cleaning = false;
	public ArrayList<Node> visited_nodes = new ArrayList<Node>();
	public int amount_of_turns = 0;
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	// Agent goes straight forward to find wall
	public Action findWall(Percept percept) {
		state.agent_last_action = state.ACTION_MOVE_FORWARD;
	    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	// Updates direction based on cardinal direction
	private void updateDirection(Percept percept, int direction) {
		if (direction == MyAgentState.EAST) {
			state.agent_direction = MyAgentState.SOUTH;
		}
		if (direction == MyAgentState.SOUTH) {
			state.agent_direction = MyAgentState.WEST;
		}
		if (direction == MyAgentState.WEST) {
			state.agent_direction = MyAgentState.NORTH;
		}
		if (direction == MyAgentState.NORTH) {
			state.agent_direction = MyAgentState.EAST;
		}
	}
	
	// Checks if a certain node is contained within a list
	public boolean listContains(ArrayList<Node> node_list, Node node) {
		for (Node n: node_list) {
			if (n.x == node.x && n.y == node.y) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;
	    System.out.println("Iteration: " + iterationCounter);
	    
	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	   
	    
	    if(!(state.world[state.agent_x_position][state.agent_y_position] == state.HOME)) { // don't change home
	    	if (dirt)
		    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
		    else
		    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);	
	    }

	    
	    state.printWorldDebug();
	    
	    //find wall at the beginning of game
	    if (!found_first_bump) {
	    	System.out.println("In first if-condition");
	    	if (!bump) {
		    	return findWall(percept);
	    	}
	    	else { // then rotate right
	    		found_first_bump = true;
	    		updateDirection(percept, state.agent_direction);
	    		state.agent_last_action = state.ACTION_TURN_RIGHT;
			    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    	}
	    }
	    
	    // find corner after first bump
	    if (!found_second_bump) {
	    	if (!bump) {
		    	return findWall(percept);
	    	}
	    	else {
	    		found_second_bump = true;
	    		
	    	}
	    }
	    
	    // is agent done with cleanup and stuck in rotation?
	    if (amount_of_turns >= 5) {
	    	// are we home?
	    	if (state.world[state.agent_x_position][state.agent_y_position] == state.HOME) {
	    		return NoOpAction.NO_OP;
	    	}
	    	// look for wall
	    	if (!found_bump_going_home) {
	    		if (!bump) {
	    			return findWall(percept);
		    	}
	    		else {
	    			found_bump_going_home = true;
	    		}
	    	}
	    	// found wall, follow wall to find home
	    	else if (bump){
	    		updateDirection(percept, state.agent_direction);
	    		state.agent_last_action = state.ACTION_TURN_RIGHT;
			    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    	}
	    	else if (!bump) {
	    		state.agent_last_action = state.ACTION_MOVE_FORWARD;
	    	    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    }
	    
	    // create node to save current coordinates
	    Node node = new Node();
	    node.x = state.agent_x_position;
	    node.y = state.agent_y_position;
	    
	    // save coordinates of node one step ahead
	    Node node_ahead = new Node();
	    if (state.agent_direction == MyAgentState.NORTH) {
	    	node_ahead.x = node.x;
	    	node_ahead.y = node.y - 1;
	    }
	    if (state.agent_direction == MyAgentState.EAST) {
	    	node_ahead.x = node.x + 1;
	    	node_ahead.y = node.y;
	    }
	    if (state.agent_direction == state.SOUTH) {
	    	node_ahead.x = node.x ;
	    	node_ahead.y = node.y + 1;
	    }
	    if (state.agent_direction == state.WEST) {
	    	node_ahead.x = node.x - 1;
	    	node_ahead.y = node.y;
	    }
	    // add current node with coordinates in visited list
	    visited_nodes.add(node);
	
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    else
	    {	
	    	if (bump) // makes agent take a circulate path
	    	{	
	    		updateDirection(percept, state.agent_direction);
	    		state.agent_last_action = state.ACTION_TURN_RIGHT;
			    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    	}
	    	else
	    	{	
	    		// turn if node ahead is already visited
	    		if (listContains(visited_nodes, node_ahead)) {
	    			updateDirection(percept, state.agent_direction);
		    		state.agent_last_action = state.ACTION_TURN_RIGHT;
		    		amount_of_turns++; // used to keep track of if clean-up is finished
		    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    		}
	    		// else go straight forward
	    		amount_of_turns = 0;
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
	    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    }
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}