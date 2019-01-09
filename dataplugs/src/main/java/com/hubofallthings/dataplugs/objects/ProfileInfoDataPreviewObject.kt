package com.hubofallthings.dataplugs.objects

data class ProfileInfoDataPreviewObject(
    val endpoint : String,
    val recordId : String,
    val data : List<Map<String,Any>>
)