package com.appliances.recycle.notice

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeApi {
    // 공지사항 목록 가져오기 (page와 size 파라미터로 페이지네이션 지원)
    @GET("api/notices")
    fun getNotices(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<Notice>>

    // 공지사항 상세 정보 가져오기
    @GET("api/notices/{nno}")
    fun getNoticeDetail(@Path("nno") nno: Long): Call<Notice>
}