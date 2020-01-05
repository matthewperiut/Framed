package dev.alexnader.framity.model

import net.fabricmc.fabric.api.renderer.v1.Renderer
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.state.property.Property
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class CustomItemFramityVoxelModel(
    private val itemMeshProvider: (Renderer) -> Mesh,
    block: Block,
    properties: List<Property<out Comparable<*>>>,
    sprite: SpriteIdentifier,
    transformerFactory: () -> MeshTransformer,
    defaultState: BlockState,
    spriteMap: Function<SpriteIdentifier, Sprite>
) : FramityVoxelModel(block, properties, sprite, transformerFactory, defaultState, spriteMap) {
    companion object {
        val of =
            { itemMeshProvider: (Renderer) -> Mesh ->
            { block: Block ->
            { properties: List<Property<out Comparable<*>>> ->
            { sprite: SpriteIdentifier ->
            { transformerFactory: () -> MeshTransformer ->
            { state: BlockState ->
            { spriteMap: Function<SpriteIdentifier, Sprite> ->
                CustomItemFramityVoxelModel(itemMeshProvider, block, properties, sprite, transformerFactory, state, spriteMap)
        }}}}}}}
    }

    private lateinit var itemMeshVal: Mesh

    private val itemMesh: Mesh
        get() {
            if (!this::itemMeshVal.isInitialized)
                this.itemMeshVal = this.itemMeshProvider(RENDERER)
            return this.itemMeshVal
        }

    override fun emitItemQuads(stack: ItemStack?, randomSupplier: Supplier<Random>?, context: RenderContext?) {
        this.transformed(context, this.transformerFactory?.invoke()?.prepare(stack, randomSupplier)) {
            context?.meshConsumer()?.accept(this.itemMesh)
        }
    }
}