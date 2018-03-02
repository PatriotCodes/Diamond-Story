package utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class TexturesLoader {
    val textureAtlas = TextureAtlas(Gdx.files.internal("graphics/atlas.atlas"))

    // GEMS:
    val redGem : TextureRegion = textureAtlas.findRegion("gems/red_gem")
    val greenGem : TextureRegion = textureAtlas.findRegion("gems/green_gem")
    val blueGem : TextureRegion = textureAtlas.findRegion("gems/blue_gem")
    val purpleGem : TextureRegion = textureAtlas.findRegion("gems/purple_gem")
    val yellowGem : TextureRegion = textureAtlas.findRegion("gems/yellow_gem")
    val superGem : TextureRegion = textureAtlas.findRegion("gems/super_gem")
    val noGem : TextureRegion = textureAtlas.findRegion("gems/no_gem")

    // CELLS:
    val tileBlank : TextureRegion = textureAtlas.findRegion("cells/tile_blank")
    val tileBlankLite : TextureRegion = textureAtlas.findRegion("cells/tile_blank_lite")
    val border = ShapeRenderer()

    // ANIMATIONS:
    val fireAnimation = ParticleEffect()
    val selectedMoveSpell : TextureRegion = textureAtlas.findRegion("selected_move_spell")
    val selectedFireSpell : TextureRegion = textureAtlas.findRegion("selected_fire_spell")
    val crossAnim : TextureRegion = textureAtlas.findRegion("cross_anim")
    val superGemGlow : TextureRegion = textureAtlas.findRegion("super_gem_glow")

    // ANIMATIONS SCALE:
    var animScaleFactor = 1f

    init {
        fireAnimation.load(Gdx.files.internal("graphics/effects/fire.p"),textureAtlas)
        animScaleFactor = Gdx.graphics.width.toFloat() / 640f // 640f is a reference width
        border.color = Color.PURPLE
    }

    private object Holder { val INSTANCE = TexturesLoader() }

    companion object {
        val instance: TexturesLoader by lazy { Holder.INSTANCE }
    }
}