package org.example

/**
 * Class to store values of the specific maze type to be used in this QLearning Project
 *
 * @param mazeDiagram a 2d array of
 */
class QLearningMaze(mazeDiagram: Array<IntArray>, startPoint: Pair<Int, Int>, endPoint: Pair<Int, Int>): Maze {
    override val mazeLayout = mazeDiagram

    override val startPos = startPoint
    override val endPos = endPoint

    override val mazeHeight = mazeLayout.size
    override val mazeWidth = mazeLayout[0].size

    override fun showMaze() {
        TODO("Not yet implemented")
    }
}