package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import enums.Axis
import enums.EffectType
import enums.JewelType
import enums.MatchType
import gameobjects.GameGrid
import gameobjects.Jewel
import gameobjects.JewelMove
import gameobjects.Match
import utils.InputHandler
import utils.TexturesLoader
import java.util.*


class GameScreen : Screen {

    private val gameGrid = GameGrid(gridTypes.circle())
    private val MAX_ROWS = gameGrid.cells.count()
    private val gemSize = Gdx.graphics.width.toFloat() / MAX_ROWS
    private val MAX_COLS = (Gdx.graphics.height.toFloat() / gemSize)
    private val batcher = SpriteBatch()
    private val gridOffset = (Gdx.graphics.height.toFloat() - (gemSize * gameGrid.cells[0].count())) / 2
    private val moves = mutableListOf<JewelMove>()
    private val itemsToCheck = mutableListOf<JewelMove>()
    private val cam = OrthographicCamera()

    private var selectedXY = Vector3()
    private var isSelected = false
    private var makeCheck = false


    init {
        cam.setToOrtho(false, MAX_ROWS.toFloat(), MAX_COLS)
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0f)
        cam.update()

        Gdx.input.inputProcessor = InputHandler(this)
    }

    override fun hide() {

    }

    override fun show() {

    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batcher.begin()
        drawGameGrid(delta)
        drawMoves(delta * 4)
        if (makeCheck)
            checkMatches()
        batcher.end()
    }

    private fun drawGameGrid(delta: Float) {
        for (i in gameGrid.cells.indices) {
            for (j in gameGrid.cells[i].indices) {
                if (gameGrid.cells[i][j].isPlaying) {
                    gameGrid.cells[i][j].draw(batcher,i.toFloat() * gemSize,
                            (j.toFloat() * gemSize) + gridOffset, gemSize, delta)
                }
            }
        }
    }

    private fun getSelected() : Vector3 {
        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat() + gridOffset, 0f)
        cam.unproject(touch)
        if (touch.x < 0)
            touch.x = -1f
        if (touch.y < 0)
            touch.y = -1f
        return Vector3(touch.x.toInt().toFloat(), touch.y.toInt().toFloat(),0f)
    }

    fun onClick() {
        if (moves.isEmpty()) {
            val testTouch = getSelected()
            if (gameGrid.inRange(testTouch.x.toInt(), testTouch.y.toInt())) {
                if (!isSelected) {
                    selectedXY = testTouch
                    isSelected = true
                    gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.isSelected = true
                } else {
                    if (gameGrid.isAdjacent(testTouch.x.toInt(), testTouch.y.toInt(), selectedXY.x.toInt(), selectedXY.y.toInt())) {

                        moves.add(JewelMove(selectedXY.x, selectedXY.y, testTouch.x, testTouch.y,
                                Jewel(gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.jewelType,gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.effect)))
                        moves.add(JewelMove(testTouch.x, testTouch.y, selectedXY.x, selectedXY.y,
                                Jewel(gameGrid.cells[testTouch.x.toInt()][testTouch.y.toInt()].jewel.jewelType,gameGrid.cells[testTouch.x.toInt()][testTouch.y.toInt()].jewel.effect)))
                        gameGrid.cells[testTouch.x.toInt()][testTouch.y.toInt()].jewel.jewelType = JewelType.NO_JEWEL
                        gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.jewelType = JewelType.NO_JEWEL
                        gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.isSelected = false
                        isSelected = false
                    } else {
                        gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.isSelected = false
                        selectedXY = testTouch
                        gameGrid.cells[selectedXY.x.toInt()][selectedXY.y.toInt()].jewel.isSelected = true
                        isSelected = true
                    }
                }
            }
        }
    }

    private fun drawMoves(delta : Float) {
        var endMove = false
        val iterator = moves.iterator()
        while (iterator.hasNext()) {
            val move = iterator.next()
            if (move.movingAxis == Axis.X) {
                if (!move.startBigger) {
                    move.xStart = move.xStart + delta
                    if (move.xStart >= move.xEnd) {
                        endMove = true
                        move.xStart = move.xEnd
                    }
                } else {
                    move.xStart = move.xStart - delta
                    if (move.xStart <= move.xEnd) {
                        endMove = true
                        move.xStart = move.xEnd
                    }
                }
            } else {
                if (!move.startBigger) {
                    move.yStart = move.yStart + delta
                    if (move.yStart >= move.yEnd) {
                        endMove = true
                        move.yStart = move.yEnd
                    }
                } else {
                    move.yStart = move.yStart - delta
                    if (move.yStart <= move.yEnd) {
                        endMove = true
                        move.yStart = move.yEnd
                    }
                }
            }
            move.jewel.draw(batcher, move.xStart * gemSize, (move.yStart * gemSize) + gridOffset,
                    gemSize, delta)
            if (endMove) {
                gameGrid.cells[move.xEnd.toInt()][move.yEnd.toInt()].jewel.jewelType = move.jewel.jewelType
                itemsToCheck.add(move)
                iterator.remove()
                endMove = false
                if (moves.isEmpty()) {
                    makeCheck = true
                }
            }
        }
    }

    private fun prepareMoves(match : List<Match>) {
        if (match.isNotEmpty()) {
            for (m in match)
                gameGrid.removeMatch(m)
            for (i in gameGrid.cells.indices) {
                 for (j in gameGrid.cells[i].indices) {
                    if (gameGrid.cells[i][j].isPlaying) {
                        if (gameGrid.cells[i][j].jewel.jewelType == JewelType.NO_JEWEL && gameGrid.cells[i][j].isPlaying) {
                            if (j < gameGrid.cells[0].count() - 1) {
                                val highestJewel = gameGrid.getHighestJewel(i, j)
                                if (highestJewel == gameGrid.cells[0].count()) {
                                    val highestNotPlaying = gameGrid.getHighestIsNotPlaying(i, j)
                                    if (highestNotPlaying == gameGrid.cells[0].count()) {
                                        moves.add(JewelMove(i.toFloat(), gameGrid.cells[0].count().toFloat(), i.toFloat(), j.toFloat(), Jewel(JewelType.from(Random().nextInt(5)),
                                                EffectType.NONE)))
                                    } else {
                                        moves.add(JewelMove(i.toFloat(), highestNotPlaying.toFloat(), i.toFloat(), j.toFloat(), Jewel(JewelType.from(Random().nextInt(5)),
                                                EffectType.NONE)))
                                    }
                                } else {
                                    moves.add(JewelMove(i.toFloat(), highestJewel.toFloat(), i.toFloat(), j.toFloat(),
                                            Jewel(gameGrid.cells[i][highestJewel].jewel.jewelType, gameGrid.cells[i][highestJewel].jewel.effect)))
                                    gameGrid.cells[i][highestJewel].jewel.jewelType = JewelType.NO_JEWEL
                                }
                            } else {
                                moves.add(JewelMove(i.toFloat(), gameGrid.cells[0].count().toFloat(), i.toFloat(), j.toFloat(), Jewel(JewelType.from(Random().nextInt(5)),
                                        EffectType.NONE)))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkMatches() {
        val listOfMatches = mutableListOf<Match>()
        val iterator = itemsToCheck.iterator()
        while (iterator.hasNext()) {
            val move = iterator.next()
            val match = gameGrid.createsMatch(move.xEnd.toInt(), move.yEnd.toInt(),
                    gameGrid.cells[move.xEnd.toInt()][move.yEnd.toInt()].jewel.jewelType)
            if (match.matchType != MatchType.NO_MATCH) {
                listOfMatches.add(match)
            } else {
                iterator.remove()
            }
        }
        makeCheck = false
        prepareMoves(listOfMatches)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {

    }
}