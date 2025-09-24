package org.duocuc.capstone_backend.service

import org.duocuc.capstone_backend.dto.MateriaDto
import org.duocuc.capstone_backend.repository.MateriaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MateriaService(
    private val materiaRepo: MateriaRepository
) {
    @Transactional(readOnly = true)
    fun listAll(): List<MateriaDto> =
        materiaRepo.findAllWithCarrera().map { m ->
            MateriaDto(
                id = m.id!!,
                name = m.name,
                carrera = m.carrera.name
            )
        }
}
