package com.reynarz.shder3D

import com.reynarz.shder3D.engine.Shader
import com.reynarz.shder3D.engine.Utils
import com.reynarz.shder3D.engine.components.MeshRenderer
import com.reynarz.shder3D.files.FileManager
import com.reynarz.shder3D.models.*
import com.reynarz.shder3D.viewmodels.HierarchyViewModel
import com.reynarz.shder3D.viewmodels.InspectorViewModel
import com.reynarz.shder3D.viewmodels.SceneViewModel
import com.reynarz.shder3D.viewmodels.ShaderEditorViewModel
import com.reynarz.shder3D.views.InspectorRecycleViewAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.*

val EngineDataModule: Module = module {

    factory {

        ShaderData("SName", UUID.randomUUID().toString()).also {
//            val unlit = Utils.getUnlitShader(1.0f)

            val unlit = Utils.getShadowMappedShader()


            it.vertexShader = unlit.first
            it.fragmentShader = unlit.second
        }
    }

    factory { MaterialData(UUID.randomUUID().toString(), get()) }
    factory { TransformComponentData() }
    factory { MeshRendererComponentData() }
    single { ProjectData("default name") }
    factory { SceneEntityData("Entity", get(), get()) }
}

val EngineComponentsModule: Module = module {
    factory { MeshRenderer() }
    factory {
        val unlitShader = Utils.getUnlitShader(1f)
        Shader(unlitShader.first, unlitShader.second)
    }
}

val GenericModule: Module = module {
    single { DefaultNavigator() }
    single { FileManager() }
}

val ViewModelsModule = module {

    single { MinityProjectRepository() }

    viewModel { HierarchyViewModel(get()) }
    viewModel { InspectorViewModel(get()) }
    viewModel { ShaderEditorViewModel(get()) }
    viewModel { SceneViewModel(get()) }

    single { InspectorRecycleViewAdapter(get(), get()) }
}