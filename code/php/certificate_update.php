<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$last_updated=$_GET['last_updated'];

$query = "select r.recruit_id, c.certificate_no, c.certificate_id, r.deleted
from recruit r inner join recruit_certificate c on r.recruit_id = c.recruit_id
where r.update_dt > '$last_updated' or r.deleted = 1";

$header = apache_request_headers();
if ( isset($header["X-Forwarded-For"]) ) {
    $ip_addr = $header['X-Forwarded-For'];
} else {
    $ip_addr = $_SERVER['REMOTE_ADDR'];
}

if($result = mysqli_query($conn, $query)) {
    $row_num = mysqli_num_rows($result);

    echo "{";

        echo "\"status\":\"OK\",";

        echo "\"rownum\":\"$row_num\",";

        echo "\"result\":";

            echo "[";

                for($i = 0; $i < $row_num; $i++) {
                    $row = mysqli_fetch_array($result);
                    echo "{";

                    echo "\"recruit_id\":\"$row[recruit_id]\", \"certificate_no\":\"$row[certificate_no]\", \"certificate_id\":\"$row[certificate_id]\", \"deleted\":\"$row[deleted]\"";

                    echo "}";
                    if($i<$row_num-1) {
                        echo ",";
                    }
                }
            echo "]";
        
    echo "}";
}
else {
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
