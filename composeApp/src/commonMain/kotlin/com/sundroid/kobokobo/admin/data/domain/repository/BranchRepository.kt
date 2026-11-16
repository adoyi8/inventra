package com.sundroid.kobokobo.admin.data.domain.repository

import com.sundroid.kobokobo.admin.data.domain.model.Branch
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface BranchRepository {

    suspend fun createBranch(branch: Branch): Result<Branch, DataError>
    suspend fun updateBranch(branch: Branch): Result<Branch, DataError>
    suspend fun deleteBranch(branchId: String, businessId: String): EmptyResult<DataError>
    suspend fun getBranchById(branchId: String, businessId: String): Result<Branch?, DataError>
    fun getBranches(businessId: String): Flow<List<Branch>>
}












