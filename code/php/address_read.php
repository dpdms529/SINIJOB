<?php
$conn = mysqli_connect(/* dbhost */, "hanium", /* dbpw */, "hanium");

$street_code=$_GET['street_code'];
$main_no=$_GET['main_no'];
$additional_no=$_GET['additional_no'];

$query = "select x, y from address
where street_code='$street_code' and main_no='$main_no' and additional_no='$additional_no'";

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
                for($i = 0; $i < $row_num; $i++){
                    $row = mysqli_fetch_array($result);
                    echo "{";
                        echo "\"street_code\":\"$street_code\", \"main_no\":\"$main_no\", \"additional_no\":\"$additional_no\"
                        , \"x\":\"$row[x]\", \"y\":\"$row[y]\"";
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
