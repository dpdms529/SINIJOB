<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$user_id=$_GET['user_id'];

$query = "delete from users
where user_id='$user_id'";

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
                        echo "\"user_id\":\"$row[user_id]\", \"street_code\":\"$row[street_code]\", \"main_no\":\"$row[main_no]\"
                        , \"additional_no\":\"$row[additional_no]\", \"name\":\"$row[name]\", \"birthday\":\"$row[birthday]\", \"age\":\"$row[age]\"
                         , \"gender\":\"$row[gender]\", \"phone_number\":\"$row[phone_number]\", \"email\":\"$row[email]\"
                         , \"address\":\"$row[address]\", \"register_dt\":\"$row[register_dt]\"";
                    echo "}";
                    if($i<$row_num-1){
                        echo ",";
                    }
                }
            echo "]";
	echo "}";
	mysqli_commit($conn);
}

else{
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
