package com.example.smarttrafficradar.features.user_profile.domain.usecase

import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfileError
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class CheckIdentifierExistsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    /**
     * Kiểm tra tính hợp lệ của mã định danh (MSSV/MSNV)
     * @param identifier Mã số sinh viên/nhân viên
     * @param email Email của người dùng (phải khớp với mã định danh trong tổ chức)
     * @param currentUid UID hiện tại (để kiểm tra xem mã đã bị người khác lấy chưa)
     * @return Result.Success(true) nếu mã hợp lệ và chưa bị ai lấy.
     */
    suspend operator fun invoke(identifier: String, email: String, currentUid: String = ""): Result<Boolean> = try {
        // 1. Kiểm tra cặp (Mã định danh + Email) có tồn tại trong danh sách tổ chức không
        val inOrg = repository.checkIdentifierInOrganization(identifier, email)
        if (!inOrg) {
            throw UserProfileError.IdentifierNotFound
        }

        // 2. Kiểm tra mã này đã bị tài khoản khác (UID khác) dùng chưa
        val isTaken = repository.isIdentifierTaken(identifier, currentUid)
        if (isTaken) {
            throw UserProfileError.IdentifierAlreadyExists
        }

        Result.success(true)
    } catch (e: UserProfileError) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(UserProfileError.UnknownError(e.message ?: "Unknown error"))
    }
}
