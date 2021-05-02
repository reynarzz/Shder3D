package com.reynarz.minityeditor

import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.models.*
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.*

val AppDataModule: Module = module {

    factory { ShaderData("", UUID.randomUUID().toString()) }
    factory { MaterialData(UUID.randomUUID().toString(), get()) }

    factory { TransformComponentData() }
    factory { MeshRendererComponentData()  }

    factory { SceneEntityData("Entity", get(), get()) }
}

val AppEngineComponents: Module = module {
    factory { MeshRenderer() }
}