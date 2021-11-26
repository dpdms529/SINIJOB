<?php
$conn = mysqli_connect(/* dbhost */, "hanium", /* dbpw */, "hanium");

$user_id=$_GET['user_id'];
$recruit_id=$_GET['recruit_id'];

$query = "insert into user_favorite
value('$user_id', '$recruit_id', now())";

$header = apache_request_headers();
if( isset($header["X-Forwarded-For"]) ) {
    $ip_addr = $header['X-Forwarded-For'];
} else {
    $ip_addr = $_SERVER['REMOTE_ADDR'];
}

if($result = mysqli_query($conn, $query)){
    echo "{";
        echo "\"status\":\"OK\",";
        echo "\"result\":";
            echo "[";
                echo "{";
                    echo "\"user_id\":\"$user_id\", \"recruit_id\":\"$recruit_id\"";
                echo "}";
            echo "]";
    echo "}";
    
    mysqli_commit($conn);

    exec("cd /home/ubuntu/workspace/ && python3 recommend.py $user_id");
}

else{
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
