package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.ArrayList;
import java.util.Random;


//Node class for making node-objects representing squares
class Node{
	public int x;
	public int y;
	public Node parent;
}

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
	
	ArrayList<Node> calculated_path = new ArrayList<Node>();
	ArrayList<Node> visited_nodes;
	ArrayList<Node> neighbour_queue; 
	boolean finished_cleaning = false;
	
	
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

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	public MyAgentState state = new MyAgentState();
	
	// Here you can define your variables!
	public int height = state.world.length;
	public int width = state.world[1].length;
	public int iterationCounter = height*width*2;
	//public int iterationCounter = 10000;
	
	public ArrayList<Node> calculated_path;
	
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
	
	//initializes lists and nodes needed for breadth-first search function
	public void calculateNewPath() {
		state.neighbour_queue = new ArrayList<Node>();
		state.visited_nodes = new ArrayList<Node>();
		
		Node initial_node = new Node();
		initial_node.x = state.agent_x_position;
		initial_node.y = state.agent_y_position;
		state.visited_nodes.add(initial_node);
		
		ArrayList<Node> neighbours = new ArrayList<Node>();
		neighbours = getMyNeighbours(initial_node); // gets neighbours of start-node
		
		for (Node n: neighbours) {
			n.parent = initial_node; // makes start-node parent of all its neighbours
			if(!(state.world[n.x][n.y] == state.WALL)) {
				state.neighbour_queue.add(n); // do not add walls in neighbour-queue
				
			}
		}
		state.calculated_path = breadthFirstSearch(initial_node);
		
	}
	
	// reverses path to find a way back home
	public ArrayList<Node> get_home_path(Node node, Node initial_node) {
		ArrayList<Node> path_to_home = new ArrayList<Node>();
		while ( node != initial_node) {
			path_to_home.add(0, node);
			node = node.parent;
		}
		return path_to_home;
		
	}
	
	public ArrayList<Node> breadthFirstSearch(Node initial_n) {
		//if all nodes has been visited
		int home;
		//Keeps track of state
        if (state.finished_cleaning) {
        	home = state.HOME;
        }else {
        	home = 0;
        }

        // no more nodes to save to path
		// if no more nodes in queue, return empty list
		if(state.neighbour_queue.isEmpty()) {
			return new ArrayList<Node>();
		}
		
		// get first node in queue (current node)
		Node first_queued_node = state.neighbour_queue.get(0);
		state.neighbour_queue.remove(0);
		
		// if clean-up is finished
		if (state.world[first_queued_node.x][first_queued_node.y] == home) {
			return get_home_path(first_queued_node, initial_n);
			
		} else {
			// is node already visited?
			if(!nodeIsInQueue(first_queued_node, state.visited_nodes)) {
				state.visited_nodes.add(first_queued_node); // add node in visited list
			}
			
			// get neighbour of current node
			ArrayList<Node> neighbours = new ArrayList<Node>();
			neighbours = getMyNeighbours(first_queued_node);
			for (Node n: neighbours) {
				// is n a valid unvisited new node?
				if(!(state.world[n.x][n.y] == state.WALL) && !nodeIsInQueue(n,state.visited_nodes) && !nodeIsInQueue(n, state.neighbour_queue)) {
					n.parent = first_queued_node;
					state.neighbour_queue.add(n); // add to queue for path
				}
			}
			
			return breadthFirstSearch(initial_n);
			
		}
		
	}

	// Checks if a certain node is contained within a list/queue
	public Boolean nodeIsInQueue(Node node, ArrayList<Node> list) {
		for (Node v_node: list) {
			if(node.x == v_node.x && node.y == v_node.y ) {
				return true;
			}
		}
		return false;
	}
	
	// returns all neighbours of node
	public ArrayList<Node> getMyNeighbours (Node n) {
		ArrayList<Node> neighbours = new ArrayList<Node>();
		
		if (n.y -1 >= 0) {
			Node neighbour1 = new Node();
			neighbour1.x = n.x;
			neighbour1.y = n.y -1;
			neighbours.add(neighbour1);
			
		}
		if (n.x -1 >= 0) {
			Node neighbour2 = new Node();
			neighbour2.x = n.x-1;
			neighbour2.y = n.y;
			neighbours.add(neighbour2);
			
		}
		if (n.x + 1 < 30) {
			Node neighbour3 = new Node();
			neighbour3.x = n.x + 1;
			neighbour3.y = n.y;
			neighbours.add(neighbour3);
			
		}
		if (n.y + 1 < 30) {
			Node neighbour4 = new Node();
			neighbour4.x = n.x;
			neighbour4.y = n.y + 1;
			neighbours.add(neighbour4);
			
		}
		return neighbours;
	}
	
	public Action doMovementNorth(Percept percept, int direction) {
		//step forward to go north
		if (direction == MyAgentState.NORTH) {
		    state.calculated_path.remove(0);
		    state.agent_last_action = state.ACTION_MOVE_FORWARD;
		    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		}
	
		//Turn right to go north
		else if (direction == MyAgentState.WEST) {
		    state.agent_direction = MyAgentState.NORTH;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
		//Turn left to go north
		else if (direction == MyAgentState.EAST) {
		    state.agent_direction = MyAgentState.NORTH;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
		    return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else {
		    state.agent_direction = MyAgentState.WEST;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			}
	}
	
	public Action doMovementWest(Percept percept, int direction) {
		//Move forward to go west
    	if (direction == MyAgentState.WEST) {
    		state.calculated_path.remove(0);
    		state.agent_last_action = state.ACTION_MOVE_FORWARD;
    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
    	}
    
    	//Turn right to go west
    	else if (direction == MyAgentState.SOUTH) {
    		state.agent_direction = MyAgentState.WEST;
    		state.agent_last_action = state.ACTION_TURN_RIGHT;
    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
    	}
		//Turn left to go west
		else if (direction == MyAgentState.NORTH) {
		    state.agent_direction = MyAgentState.WEST;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
		    return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else {
		    state.agent_direction = MyAgentState.SOUTH;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
	}
	
	public Action doMovementEast(Percept percept, int direction) {
		//Move forward to go west
		if (direction == MyAgentState.EAST) {
		    state.calculated_path.remove(0);
		    state.agent_last_action = state.ACTION_MOVE_FORWARD;
		    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		}
		//Turn right to go east
		else if (direction == MyAgentState.NORTH) {
		    state.agent_direction = MyAgentState.EAST;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
		//Turn left to go east
		else if (direction == MyAgentState.SOUTH) {
		    state.agent_direction = MyAgentState.EAST;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
		    return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else {
		    state.agent_direction = MyAgentState.NORTH;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
	}
	
	public Action doMovementSouth(Percept percept, int direction) {
		//Move forward to go south
		if (direction == MyAgentState.SOUTH) {
		    state.calculated_path.remove(0);
		    state.agent_last_action = state.ACTION_MOVE_FORWARD;
		    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		}
		//Turn right to go south
		else if (direction == MyAgentState.EAST) {
		    state.agent_direction = MyAgentState.SOUTH;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
		//Turn left to go south
		else if (direction == MyAgentState.WEST) {
		    state.agent_direction = MyAgentState.SOUTH;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
		    return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else {
		    state.agent_direction = MyAgentState.EAST;
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
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
	    
	    // Next action selection based on the percept value
	    if (dirt){
	    	// Suck up the dirt
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } else{
	    	// If path is empty then calculate a new path
	    	if(state.calculated_path.isEmpty()) {
	    		calculateNewPath();
	    		// If calculated path is empty then the whole area has been visited
	    		if(state.calculated_path.isEmpty()) {
	    			if(state.world[state.agent_x_position][state.agent_y_position] == state.HOME) {
	    				return NoOpAction.NO_OP; //Be idle if already home
	    			}else {
	    				state.finished_cleaning = true;
	    				calculateNewPath(); // Calculate path home
	    			}
	    		}
	    	}
	    	
	    	Node next_step = state.calculated_path.get(0); // first node in path
	    	
	    	// Which direction do we want agent to go?
	    	int x_difference = next_step.x - state.agent_x_position; 
	    	int y_difference = next_step.y - state.agent_y_position;
	    
	    	// Go north
		    if (x_difference == 0 && y_difference == -1) {
		    	return doMovementNorth(percept, state.agent_direction);
			}
			
		    // Go west
		    else if (x_difference == -1 && y_difference == 0) {
		    	return doMovementWest(percept, state.agent_direction);
		    }
		  
		    // Go east
		    else if (x_difference == 1 && y_difference == 0) {
		    	return doMovementEast(percept, state.agent_direction);
		    }

		    // Go south
		    else if (x_difference == 0 && y_difference == 1) {
		    	return doMovementSouth(percept, state.agent_direction);
		    }
		}
		return NoOpAction.NO_OP;
	    }
	    
	}


public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}