<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$last_updated=$_GET['last_updated'];

$query = "select r.recruit_id, r.title, r.organization, r.salary_type_code, r.salary, a.b_dong_code, r.job_code, 
r.career_required, r.career_min, r.enrollment_code, r.certificate_required, a.x, a.y, r.update_dt , r.deleted from recruit r
inner join address a on r.street_code = a.street_code and r.main_no = a.main_no and r.additional_no = a.additional_no
where update_dt > '$last_updated' or deleted = 1";

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

        echo "\"rownum\":\"$row_num\",";

        echo "\"result\":";

            echo "[";

                for($i = 0; $i < $row_num; $i++){
                    $row = mysqli_fetch_array($result);
                    echo "{";

                    echo "\"recruit_id\":\"$row[recruit_id]\", \"title\":\"$row[title]\", \"organization\":\"$row[organization]\", \"salary_type_code\":\"$row[salary_type_code]\", \"salary\":\"$row[salary]\", \"b_dong_code\":\"$row[b_dong_code]\", \"job_code\":\"$row[job_code]\", \"career_required\":\"$row[career_required]\", \"career_min\":\"$row[career_min]\", \"enrollment_code\":\"$row[enrollment_code]\", \"certificate_required\":\"$row[certificate_required]\", \"x\":\"$row[x]\", \"y\":\"$row[y]\", \"update_dt\":\"$row[update_dt]\", \"deleted\":\"$row[deleted]\"";

                    echo "}";
                    if($i<$row_num-1){
                        echo ",";
                    }
                }



            echo "]";

    echo "}";
}

else{
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
