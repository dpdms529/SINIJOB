<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$query = "select max(update_dt) as max from recruit";

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

                    echo "\"last_updated\":\"$row[max]\"";

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
