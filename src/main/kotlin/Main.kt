package org.example

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {

    // initialize the maze layout that we want to use for our tests
    // 1s represent walls and 0s represent empty spaces
    val mazeLayout = arrayOf(
        intArrayOf(0, 1, 0, 0, 0),
        intArrayOf(0, 1, 1, 1, 0),
        intArrayOf(0, 0, 0, 1, 0),
        intArrayOf(1, 1, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0)
    )

    // set the starting and ending points of the maze
    val startingPoint = Pair(0, 0)
    val endingPoint = Pair(4, 4)

    // create a Q Learning Maze object
    val maze = QLearningMaze(mazeLayout, startingPoint, endingPoint)

    // actions the agent can take: down, up, right, left
    val actions = arrayOf(
        Pair(-1, 0), // down
        Pair(1, 0), // up
        Pair(0, 1), // right
        Pair(0, -1)) // left

}