package com.luisansal.jetpack.domain.usecases

import com.luisansal.jetpack.core.utils.EMPTY
import com.luisansal.jetpack.data.repository.VisitRepository
import com.luisansal.jetpack.domain.entity.VisitEntity

class VisitUseCase(private val userUseCase: UserUseCase,private val visitRepository: VisitRepository) {

    fun getByUser(email: String): List<VisitEntity>? {
        return visitRepository.getByUser(email)
    }

    fun saveOneVisitForUser(visitEntity: VisitEntity, userId: String): Boolean {
        visitRepository.deleteAllByUser(userId)
        val visitId = visitRepository.save(visitEntity);
        return visitRepository.saveUser(visitId, userId)
    }
}