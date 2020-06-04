# Distributed Multi-Agent System Simulation Platform

This platform allows users to simulate a distributed-multi agent system.

To begin working with this platform, navigate to the 
[simulations](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/simulations) 
directory.  This "DSL" (domain-specific language) allows you to easily define algorithms and specify the parameers of the simulation, 
for instance in the files [DiscreteGreedy2D.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/simulations/DiscreteGreedy2D.kt)
 and [DiscreteLLL2D.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/simulations/DiscreteLLL2D.kt)
 - **To run the project, run one of these two files and go to** [this page](localhost:8080/static/test.html)
 - Note: The complete algorithms and system definitions for both of these are in [Algorithms.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/simulations/Algorithms.kt) and [Systems.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/simulations/Systems.kt). The algorithms have further utility functions in [IterativeDiscreteUtils.kt
](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/simulation/IterativeDiscreteUtils.kt)
- The [IterativeDiscreteSimulation](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/simulation/IterativeDiscreteSimulation.kt) class (created with the .create{}) is the only current implementation of [NodeSimulation](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/simulation/NodeSimulation.kt). To create an IterativeDiscreteSimulation, there must be three lambdas defined within the create{} lambda:
    - `system{}`, which defines the number of agents, their coordinate bounds, starting locations, and weights map
    - `visualization{}`, where additional elements can be added to the visualization (note: the changes must also be made in the JS code, this just adds an endpoint to the server)
    - `algorithm{}`, which is a periodic function that runs on each node at the interval specified
    
Regarding how the platform is actually implemented and the backend structure of it, navigate to  [infrastructure](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/infrastructure). The platform uses gRPC to communicate between the nodes, each of which are in their own individual thread. Because this is a decentralized system, there isn't a single server managing the location data of all the other nodes. Instead, each node "broadcasts" any changes it makes to every other node, and the server is simply used as a vehicle to make that possible in this simulation.
- The [node](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/infrastructure/server) directory contains the code for managing all of the data behind a given node. The [Node](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/node/Node.kt) interface exists so that future backend implementations of communication can be put in place without changing the ultimate user experience with the DSL. The [GrpcNode](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/node/GrpcNode.kt) class is the current implementation of this, and it manages all of the bookkeeping for keeping track of the weights of each location, and the locations of every node, as well as sending out updates to the location for its node. All of the functions in the system{] block are used to create this Node object, and the algorithm{} block is directly calling functions in this class (e.g. putLocation() which broadcasts the location of the node to every other node)  
- The [server](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/infrastructure/server) directory contains the code that forwards location and weight data updates to all other drones. The file with actual logic to do so is [CommunicationServiceImpl.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/server/CommunicationServiceImpl.kt)
- The [common](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common) directory contains some useful classes and functions, including:
    - [Coordinate](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common/Coordinate.kt): self explanatory
    - [CoordinateArea](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common/CoordinateArea.kt): defines a cubic region which the nodes are bounded by
    - [SharedInfo](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common/SharedInfo.kt) (interface) and [MapSharedInfo](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common/MapSharedInfo.kt) (implementation): Misleading name, should be called something like "LocalInfo": this is the local bookkeeping object for each node
    - [WeightsMap](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common/WeightsMap.kt): Keeps track of weights locally, allows weights to be imported from .csv file 
    - [NumberExtensions](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/common/NumberExtensions.kt): some useful functions that add on to the Number classes
- The [visualization](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/infrastructure/visualization) directory contains the Ktor server that actually visualizes the locations of the nodes. It posts [this HTML page](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/resources/static/test.html) with this [JS code](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/resources/static/dronesTest.js) which uses p5.js to create the visualization. )
- The [simulation](https://github.com/ashah03/kotlin-distributed-framework/tree/master/src/main/kotlin/com/aditshah/distributed/infrastructure/simulation) directory is where the core simulation code is located
    - The [NodeSimulation.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/simulation/NodeSimulation.kt) file contains the core of what happens when the simulation is run: The communication server is spun up, aiong with the visualization server (which is actually atached to a random node so it has access to internal data), and each of the nodes are spun up in their own threads.
    - This works in conjunction with the [IterativeDiscreteSimulation.kt](https://github.com/ashah03/kotlin-distributed-framework/blob/master/src/main/kotlin/com/aditshah/distributed/infrastructure/simulation/IterativeDiscreteSimulation.kt), which is one implementation of the NodeSimulation abstract class. It implements the start function, which in this case periodically runs whatever function the user included in the algorithm. There could be many such classes which describe different types of multi-agent system algorithms; an iterative periodic approach is only one of them.


 

