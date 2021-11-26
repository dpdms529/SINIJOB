<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$recruit_id=$_GET['recruit_id'];

$query = "select * from recruit inner join job_category
on recruit.job_code=job_category.category_code inner join enrollment_status 
on recruit.enrollment_code=enrollment_status.enrollment_code inner join required_check
on recruit.career_required=required_check.check_code inner join salary_type
on recruit.salary_type_code=salary_type.salary_type_code inner join education
on recruit.min_education_code=education.education_code
where recruit.recruit_id='$recruit_id'";

$header = apache_request_headers();
if( isset($header["X-Forwarded-For"]) ) {
    $ip_addr = $header['X-Forwarded-For'];
} else {
    $ip_addr = $_SERVER['REMOTE_ADDR'];
}

if($result = mysqli_query($conn, $query)){
    $row_num = mysqli_num_rows($result);

    echo "{";

        echo "\"status\":\"OK\",";

        echo "\"result\":";

            echo "[";

                $row = mysqli_fetch_array($result);
                echo "{";

                echo "\"recruit_id\":\"$row[recruit_id]\", \"title\":\"$row[title]\", \"content\":\"$row[content]\", \"close_date\":\"$row[close_date]\", \"organization\":\"$row[organization]\"
                , \"contact\":\"$row[contact]\", \"basic_address\":\"$row[basic_address]\", \"detail_address\":\"$row[detail_address]\", \"url\":\"$row[url]\"
                , \"salary_type_name\":\"$row[salary_type_name]\", \"salary\":\"$row[salary]\", \"work_day\":\"$row[work_day]\"
                , \"education_scope\":\"$row[education_scope]\", \"num_of_people\":\"$row[num_of_people]\", \"computer_able\":\"$row[computer_able]\"
                , \"preference_cond\":\"$row[preference_cond]\", \"etc_preference_cond\":\"$row[etc_preference_cond]\", \"screening_process\":\"$row[screening_process]\"
                , \"register_method\":\"$row[register_method]\", \"submission_doc\":\"$row[submission_doc]\", \"etc_info\":\"$row[etc_info]\"
                , \"work_time\":\"$row[work_time]\", \"four_insurence\":\"$row[four_insurence]\", \"retire_pay\":\"$row[retire_pay]\", \"required\":\"$row[required]\"
                , \"etc_welfare\":\"$row[etc_welfare]\", \"representative\":\"$row[representative]\", \"industry\":\"$row[industry]\", \"corp_address\":\"$row[corp_address]\"
                , \"total_worker\":\"$row[total_worker]\", \"sales_amount\":\"$row[sales_amount]\", \"career_min\":\"$row[career_min]\"
, \"enrollment_name\":\"$row[enrollment_name]\", \"category_name\":\"$row[category_name]\"";

                echo "}";

            echo "]";

    echo "}";
}

else{
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
