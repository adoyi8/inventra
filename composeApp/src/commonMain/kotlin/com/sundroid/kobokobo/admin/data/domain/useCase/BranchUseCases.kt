package com.sundroid.kobokobo.admin.data.domain.useCase

import com.sundroid.kobokobo.admin.data.domain.model.Branch
import com.sundroid.kobokobo.admin.data.domain.repository.BranchRepository
import kotlinx.coroutines.flow.Flow

class CreateBranchUseCase(private val repo: BranchRepository) {
    suspend operator fun invoke(branch: Branch) = repo.createBranch(branch)
}

class UpdateBranchUseCase(private val repo: BranchRepository) {
    suspend operator fun invoke(branch: Branch) = repo.updateBranch(branch)
}

class DeleteBranchUseCase(private val repo: BranchRepository) {
    suspend operator fun invoke(branchId: String, businessId: String) = repo.deleteBranch(branchId, businessId)
}

class GetBranchByIdUseCase(private val repo: BranchRepository) {
    suspend operator fun invoke(branchId: String, businessId: String) = repo.getBranchById(branchId, businessId)
}

class GetBranchesUseCase(private val repo: BranchRepository) {
    operator fun invoke(businessId: String): Flow<List<Branch>> = repo.getBranches(businessId)
}












