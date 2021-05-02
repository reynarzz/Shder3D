package com.reynarz.minityeditor

import com.reynarz.minityeditor.engine.Shader
import com.reynarz.minityeditor.engine.Utils
import com.reynarz.minityeditor.engine.components.MeshRenderer
import com.reynarz.minityeditor.models.*
import com.reynarz.minityeditor.viewmodels.HierarchyViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.*

val EngineDataModule: Module = module {

    factory { ShaderData("", UUID.randomUUID().toString()) }
    factory { MaterialData(UUID.randomUUID().toString(), get()) }
    factory { TransformComponentData() }
    factory { MeshRendererComponentData() }
    factory { SceneEntityData("Entity", get(), get()) }
}

val EngineComponentsModule: Module = module {
    factory { MeshRenderer() }
    factory {
        val unlitShader = Utils.getUnlitShader(1f)
        Shader(unlitShader.first, unlitShader.second)
    }
}

val ViewModelsModule: Module = module{

    single { HierarchyViewModel() }

}