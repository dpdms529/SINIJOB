<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$recruit_id=$_GET['recruit_id'];

$query = "select * from recruit inner join address 
on recruit.street_code=address.street_code
and recruit.main_no=address.main_no
and recruit.additional_no=address.additional_no
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

                echo "\"x\":\"$row[x]\", \"y\":\"$row[y]\"";

                echo "}";

            echo "]";

    echo "}";
}

else{
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
