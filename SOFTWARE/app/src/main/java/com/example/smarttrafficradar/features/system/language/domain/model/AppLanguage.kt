<<<<<<< HEAD
package com.example.smarttrafficradar.features.system.language.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    VIETNAMESE("vi");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return AppLanguage.entries.firstOrNull { it.code == code } ?: ENGLISH
        }
    }
=======
package com.example.smarttrafficradar.features.system.language.domain.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    VIETNAMESE("vi");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return AppLanguage.entries.firstOrNull { it.code == code } ?: ENGLISH
        }
    }
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}