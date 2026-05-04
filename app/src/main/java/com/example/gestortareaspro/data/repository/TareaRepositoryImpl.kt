package com.example.gestortareaspro.data.repository

import com.example.gestortareaspro.data.local.TareaDao
import com.example.gestortareaspro.data.local.TareaEntity
import com.example.gestortareaspro.domain.model.Tarea
import com.example.gestortareaspro.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación concreta del repositorio.
 * Realiza el mapeo entre TareaEntity (Room) y Tarea (dominio),
 * manteniendo la capa de dominio libre de dependencias externas.
 */
class TareaRepositoryImpl(
    private val tareaDao: TareaDao
) : TareaRepository {

    override fun obtenerTodas(): Flow<List<Tarea>> {
        return tareaDao.obtenerTodas().map { entidades ->
            entidades.map { it.aModelo() }
        }
    }

    override suspend fun insertar(tarea: Tarea) {
        tareaDao.insertar(TareaEntity.desdeModelo(tarea))
    }

    override suspend fun actualizar(tarea: Tarea) {
        tareaDao.actualizar(TareaEntity.desdeModelo(tarea))
    }

    override suspend fun eliminar(tarea: Tarea) {
        tareaDao.eliminar(TareaEntity.desdeModelo(tarea))
    }
}
