package org.techtown.hanieum.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Recruit;

import java.util.List;

@Dao
public interface RecruitDao {
    @Query("SELECT * FROM recruit")
    LiveData<List<Recruit>> getAll();

    @Query("SELECT MAX(update_dt) FROM recruit")
    List<String> getLastUpdated();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewRecruit(Recruit newRecruit);

    @Query("DElETE FROM recruit WHERE recruit_id = :goneId")
    void deleteGoneRecruit(String goneId);

    @Query("select * from recruit as r where r.recruit_id = :id")
    List<Recruit> getList(String id);

    //근무형태만 선택
    @Query("select * from recruit as r where r.enrollment_code = :enrollment_code")
    LiveData<List<Recruit>> getFilteredList1(String enrollment_code);

    //직종만 선택
    @Query("select * from recruit as r\n" +
            "where r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code))")
    LiveData<List<Recruit>> getFilteredList2(List<String> primary_cate_code);

    //직종, 근무형태만 선택
    @Query("select * from recruit as r\n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList3(List<String> primary_cate_code, String enrollment_code);

    //지역만 선택
    @Query("select * from recruit as r \n" +
            "where r.b_dong_code in (:b_dong_code)")
    LiveData<List<Recruit>> getFilteredList4(List<String> b_dong_code);

    //지역, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList5(List<String> b_dong_code, String enrollment_code);

    //지역, 직종만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))")
    LiveData<List<Recruit>> getFilteredList6(List<String> b_dong_code, List<String> primary_cate_code);

    //지역, 직종, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList7(List<String> b_dong_code, List<String> primary_cate_code, String enrollment_code);

    //경력만 선택
    @Query("select * from recruit as r \n" +
            "where (r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2")
    LiveData<List<Recruit>> getFilteredList8(String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10);

    //경력, 근무형태만 선택
    @Query("select * from recruit as r\n" +
            "where ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2)\n" +
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList9(String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code);

    //경력, 직종만 선택
    @Query("select * from recruit as r\n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2)")
    LiveData<List<Recruit>> getFilteredList10(List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10);

    //경력, 직종, 근무형태만 선택
    @Query("select * from recruit as r\n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)"+
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList11(List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code);

    //경력, 지역만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2)")
    LiveData<List<Recruit>> getFilteredList12(List<String> b_dong_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10);

    //경력, 지역, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList13(List<String> b_dong_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code);

    //경력, 지역, 직종만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2)")
    LiveData<List<Recruit>> getFilteredList14(List<String> b_dong_code, List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10);

    //경력, 지역, 직종, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.enrollment_code = :enrollment_code)")
    LiveData<List<Recruit>> getFilteredList15(List<String> b_dong_code, List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code);

    //자격증만 선택
    @Query("select * from recruit as r \n" +
            "where r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id))")
    LiveData<List<Recruit>> getFilteredList16(List<String> certificate_id);

    //자격증, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.enrollment_code = :enrollment_code) \n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList17(List<String> certificate_id, String enrollment_code);

    //자격증, 직종만 선택
    @Query("select * from recruit as r \n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList18(List<String> primary_cate_code, List<String> certificate_id);

    //자격증, 직종, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList19(List<String> primary_cate_code, String enrollment_code, List<String> certificate_id);

    //자격증, 지역만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList20(List<String> b_dong_code, List<String> certificate_id);

    //자격증, 지역, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList21(List<String> b_dong_code, String enrollment_code, List<String> certificate_id);

    //자격증, 지역, 직종만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList22(List<String> b_dong_code, List<String> primary_cate_code, List<String> certificate_id);

    //자격증, 지역, 직종, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList23(List<String> b_dong_code, List<String> primary_cate_code, String enrollment_code, List<String> certificate_id);

    //경력, 자격증만 선택
    @Query("select * from recruit as r \n" +
            "where ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList24(String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, List<String> certificate_id);

    //경력, 자격증, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList25(String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code, List<String> certificate_id);

    //경력, 자격증, 직종만 선택
    @Query("select * from recruit as r \n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList26(List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, List<String> certificate_id);

    //경력, 자격증, 직종, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList27(List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code, List<String> certificate_id);

    //경력, 자격증, 지역만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)));")
    LiveData<List<Recruit>> getFilteredList28(List<String> b_dong_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, List<String> certificate_id);

    //경력, 자격증, 지역, 근무형태만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList29(List<String> b_dong_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code, List<String> certificate_id);

    //경력, 자격증, 지역, 직종만 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList30(List<String> b_dong_code, List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, List<String> certificate_id);

    //모두 선택
    @Query("select * from recruit as r \n" +
            "where (r.b_dong_code in (:b_dong_code))\n" +
            "and (r.job_code in (select category_code from job_category where primary_cate_code in (:primary_cate_code)))\n" +
            "and ((r.career_required = 2 and ((r.job_code = :job_code1 and r.career_min <= :career1) or (r.job_code = :job_code2 and r.career_min <= :career2) \n" +
            "or (r.job_code = :job_code3 and r.career_min <= :career3) or (r.job_code = :job_code4 and r.career_min <= :career4) \n" +
            "or (r.job_code = :job_code5 and r.career_min <= :career5) or (r.job_code = :job_code6 and r.career_min <= :career6) \n" +
            "or (r.job_code = :job_code7 and r.career_min <= :career7) or (r.job_code = :job_code8 and r.career_min <= :career8) \n" +
            "or (r.job_code = :job_code9 and r.career_min <= :career9) or (r.job_code = :job_code10 and r.career_min <= :career10))) \n" +
            "or r.career_required < 2 \n)" +
            "and (r.enrollment_code = :enrollment_code)\n" +
            "and (r.certificate_required = 0\n" +
            "or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificate_id)))")
    LiveData<List<Recruit>> getFilteredList31(List<String> b_dong_code, List<String> primary_cate_code, String job_code1, int career1,String job_code2, int career2,String job_code3, int career3,String job_code4, int career4,String job_code5, int career5,String job_code6, int career6,String job_code7, int career7,String job_code8, int career8,String job_code9, int career9,String job_code10, int career10, String enrollment_code, List<String> certificate_id);


}
