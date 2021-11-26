<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$user_id=$_GET['user_id'];

$query = "select * from recommendation_list
where user_id='$user_id' order by similarity desc";

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
		    	echo "\"user_id\":\"$row[user_id]\", \"recruit_id\":\"$row[recruit_id]\", \"similarity\":\"$row[similarity]\"";
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
