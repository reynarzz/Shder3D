package com.reynarz.minityeditor

import com.reynarz.minityeditor.models.ProjectData
import org.koin.java.KoinJavaComponent.get

// This is a singleton
class FakeDataBase private constructor() {

    val projectData : ProjectData = get(ProjectData::class.java)

    companion object {
        @Volatile
        private var instance: FakeDataBase? = null

        fun getInstance() : FakeDataBase {

            if(instance != null){
                return instance!!
            }
            else{
                instance = FakeDataBase()
            }

            return instance!!
        }
    }
}