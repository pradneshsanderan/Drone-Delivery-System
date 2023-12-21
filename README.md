# Drone Delivery System
## About
In this project, I devised and implemented an algorithm to model a drone's flight path, making deliveries from synthetic test data to check if a
drone delivery system would be feasible and if the drone could complete all orders for the day before running out of battery. To do this, I optimised the greedy heuristic, Dijkstra's algorithm and christofides algorithm to get the best flight path that completed the delivery
for 99% of the test data.
## Technology Used
 - Java
 - Apache Database
 - GeoJSON
## How it works
### Task
Devise and implement an algorithm to model a drone's flight path, making deliveries from synthetic test data to check if a
drone delivery system would be feasible and if the drone could complete all orders for the day before running out of battery.
### The Algorithm
The algorithm works such that the route is calculated and its movements are returned. The
algorithm that calculates the shortest path is simple but the graph that allows this algorithm to
work is what can be confusing
#### Graph
I had initially wanted to use the Christofides algorithm as a TSP algorithm to solve the
problem of which order to complete first. For that algorithm to work, I would need to create a
graph of equilateral triangles made up of nodes and edges whereby for each node, it would have at least 2 neighbouring
node which will form an equilateral triangle. I then added all of the nodes and edges to a graph. I then checked if each node and edge on
the graph was valid. A node or edge is valid if it is in the confinement area,not in the no fly
zone and does not cross the no fly zone. If a node or edge is invalid, then I would remove the
node or edge entirely. Since there are no nodes or edges in the no fly zone or outside the
confinement area, the drone cannot traverse there,hence it is impossible to be in an invalid
location.
#### Traversal
To traverse the graph the drone first had to find the nodes close to the locations it wants to go
to.Once the nodes are found, the drone can find the shortest path. It uses one of the TSP
algorithms to find the most optimum order of the orders to complete. The drone uses Dijkstra
shortest path algorithm from jgrapht to find the shortest path on the graph I had previously
between 2 points. I had tried using Astar shortest path algorithm with the eucleadian distance
as the admissible heuristic but it did not work any much better than Dijkstra so I stuck with
Dijkstra (you can use the A* algorithm by uncommenting it in the code and commenting the
Dijkstra line)While the drone is moving between orders, the index of the nodes in the list of nodes when
the drone hovers and moves on to the next order is recorded. This will be used later when
writing the data to the database
The algorithm is simple as for each order I just need to find the path with the shortest distance
between the pick up and delivery locations and add it to the list of movement. I do not need to
worry about the drone moving into the no-fly zone or out of the confinement area as I know
that would never happen since I have already removed those nodes and edges.

#### The TSP Algorithms
To maximise the amount of money earned per day I would need to use an algorithm that
would prioritise certain orders over the other. I had initially wanted to use Christofides
algorithm as it seemed to have the best results. However, due to a lack of time I was unable to
implement it. Instead I implemented 3 others which are the greedy approach,nearest
neighbour approach and lastly the random approach.
The greedy approach seemed to be the best among the three as it prioritised the money earned
which is the main objective of the system. Even on the day with the greatest number of orders
which is 27/12/2023 the percentage of monetary value is 84% which is higher than the other
algorithms. Hence, the greedy approach gave the most optimal order or orders that the drone
should complete. I also tested each approach on 100 different days and plotted a scatter
diagram to convey my results. From the diagram it is obvious that the greedy approach and
nearest neighbour approach have similar results.

<img  src="./Screenshot 2023-12-16 234120.png"/>

## Images

<img  src="./Screenshot 2023-12-18 191915.png"/>
Drone path on 11/11/2022

<img  src="./Screenshot 2023-12-20 210500.png"/>
Drone path on 12/12 /2022
