import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

// Fire renderer ported from https://fabiensanglard.net/doom_fire_psx/

private const val VIEW_WIDTH = 320
private const val VIEW_HEIGHT = 168
private const val MAX_X = VIEW_WIDTH - 1
private const val MAX_Y = VIEW_HEIGHT - 1

// Colors from https://github.com/fabiensanglard/DoomFirePSX/blob/master/flames.html
private val FIRE_RGBS = arrayOf(
    Triple(0x00, 0x00, 0x00),
    Triple(0x07, 0x07, 0x07),
    Triple(0x1F, 0x07, 0x07),
    Triple(0x2F, 0x0F, 0x07),
    Triple(0x47, 0x0F, 0x07),
    Triple(0x57, 0x17, 0x07),
    Triple(0x67, 0x1F, 0x07),
    Triple(0x77, 0x1F, 0x07),
    Triple(0x8F, 0x27, 0x07),
    Triple(0x9F, 0x2F, 0x07),
    Triple(0xAF, 0x3F, 0x07),
    Triple(0xBF, 0x47, 0x07),
    Triple(0xC7, 0x47, 0x07),
    Triple(0xDF, 0x4F, 0x07),
    Triple(0xDF, 0x57, 0x07),
    Triple(0xDF, 0x57, 0x07),
    Triple(0xD7, 0x5F, 0x07),
    Triple(0xD7, 0x5F, 0x07),
    Triple(0xD7, 0x67, 0x0F),
    Triple(0xCF, 0x6F, 0x0F),
    Triple(0xCF, 0x77, 0x0F),
    Triple(0xCF, 0x7F, 0x0F),
    Triple(0xCF, 0x87, 0x17),
    Triple(0xC7, 0x87, 0x17),
    Triple(0xC7, 0x8F, 0x17),
    Triple(0xC7, 0x97, 0x1F),
    Triple(0xBF, 0x9F, 0x1F),
    Triple(0xBF, 0x9F, 0x1F),
    Triple(0xBF, 0xA7, 0x27),
    Triple(0xBF, 0xA7, 0x27),
    Triple(0xBF, 0xAF, 0x2F),
    Triple(0xB7, 0xAF, 0x2F),
    Triple(0xB7, 0xB7, 0x2F),
    Triple(0xB7, 0xB7, 0x37),
    Triple(0xCF, 0xCF, 0x6F),
    Triple(0xDF, 0xDF, 0x9F),
    Triple(0xEF, 0xEF, 0xC7),
    Triple(0xFF, 0xFF, 0xFF)
)
private const val BLACK_INDEX = 0
private val WHITE_INDEX = FIRE_RGBS.size - 1

private val FIRE_PALLETE = FIRE_RGBS.map { triple ->
    Color(triple.first / 255.0, triple.second / 255.0, triple.third / 255.0, 1.0)
}

private val FPS = 60L
private val NS_PER_FRAME: Long = 1_000_000_000 / FPS

class DoomFireView : View("DoomFire.kt") {
    private val buffer = IntArray(VIEW_WIDTH * VIEW_HEIGHT)
    private operator fun IntArray.get(x: Int, y: Int) = buffer[y * VIEW_WIDTH + x]
    private operator fun IntArray.set(x: Int, y: Int, value: Int) { buffer[y * VIEW_WIDTH + x] = value }
    private var isFireOn = false
    init {
        assert(!isFireOn)
        toggleFire()
    }

    private var canvas: Canvas by singleAssign()
    private val timer = object : AnimationTimer() {
        private var last = Long.MIN_VALUE
        private var elapsed = 0L
        override fun handle(now: Long) {
            if (last == Long.MIN_VALUE) {
                last = now
                return
            }

            elapsed += (now - last)
            elapsed = min(elapsed, NS_PER_FRAME * 10) // Clamp in case we miss a ton of frames, e.g. breakpoint
            last = now
            var dirty = false
            while (elapsed > NS_PER_FRAME) {
                elapsed -= NS_PER_FRAME
                step()
                dirty = true
            }
            if (dirty) {
                updateCanvas()
            }
        }
    }.start()


    override val root = group {
        canvas = Canvas(MAX_X.toDouble(), MAX_Y.toDouble()).apply {
            scaleX = 3.0
            scaleY = 3.0
        }
        updateCanvas()
        add(canvas)

        keyboard {
            addEventHandler(KeyEvent.KEY_PRESSED) {
                if (it.code == KeyCode.SPACE) toggleFire()
            }
        }
    }

    private fun toggleFire() {
        isFireOn = !isFireOn
        if (isFireOn) {
            for (x in 0..MAX_X) {
                buffer[x, MAX_Y] = WHITE_INDEX
            }
        }
    }

    private fun step() {
        if (!isFireOn) {
            // Decaying the source of the fire will eventually starve the rest of it
            for (x in 0..MAX_X) {
                buffer[x, MAX_Y] = max(0, buffer[x, MAX_Y] - Random.nextFloat().roundToInt())
            }
        }

        for (y in 0 until MAX_Y) { // Always leave the last Y line alone, it's the source of the fire
            for (x in 0..MAX_X) {
                val srcColorIndex = buffer[x, y + 1]
                var dstColorIndex = 0
                var xFinal = x
                if (srcColorIndex != 0) {
                    val decayRandomness = (Random.nextFloat() * 2.0).toInt()
                    val xOffsetRandomness = (Random.nextFloat() * 3.0).toInt() - 1 // Windy to the left
                    dstColorIndex = (srcColorIndex - decayRandomness).coerceIn(BLACK_INDEX, WHITE_INDEX)
                    xFinal = (x - xOffsetRandomness + VIEW_WIDTH) % VIEW_WIDTH // Wrap x
                }
                buffer[xFinal, y] = dstColorIndex
            }
        }
    }

    private fun updateCanvas() {
        val gc = canvas.graphicsContext2D
        gc.fill = FIRE_PALLETE[BLACK_INDEX] 
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)
        for (y in 0..MAX_Y) {
            for (x in 0..MAX_X) {
                buffer[x, y].takeIf { it != 0 }?.let { colorIndex ->
                    gc.pixelWriter.setColor(x, y, FIRE_PALLETE[colorIndex])
                }
            }
        }
    }
}

class DoomFireApp : App(DoomFireView::class)
fun main(args: Array<String>) {
    launch<DoomFireApp>(args)
}
