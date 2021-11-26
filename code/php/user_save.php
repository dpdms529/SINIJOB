<?php
$conn = mysqli_connect(/* dbhost */,"hanium",/* dbpw */,"hanium");

$user_id=$_GET['user_id'];
$street_code=$_GET['street_code'];
$main_no=$_GET['main_no'];
$additional_no=$_GET['additional_no'];
$name=$_GET['name'];
$age=$_GET['age'];
$gender=$_GET['gender'];
$phone_number=$_GET['phone_number'];
$email=$_GET['email'];
$address=$_GET['address'];
$birthday=$_GET['birthday'];
$keyword=$_GET['keyword'];

$query = "insert into users (user_id, street_code, main_no, additional_no, name, age, gender, phone_number, email, address, register_dt, birthday, keyword)
value('$user_id', '$street_code', '$main_no', '$additional_no', '$name', '$age', '$gender', '$phone_number', '$email', '$address', now(), '$birthday', '$keyword')";

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
                    echo "\"user_id\":\"$user_id\", \"street_code\":\"$street_code\", \"main_no\":\"$main_no\", \"additional_no\":\"$additional_no\", \"name\":\"$name\"
                    , \"age\":\"$age\", \"gender\":\"$gender\", \"phone_number\":\"$phone_number\", \"email\":\"$email\", \"address\":\"$address\", \"register_dt\":\"$row[register_dt]\"
                    , \"birthday\":\"$birthday\", \"keyword\":\"$keyword\"";
                echo "}";
            echo "]";
	echo "}";

	mysqli_commit($conn);

	exec("cd /home/ubuntu/workspace/ && python3 recommend_fasttext.py $user_id");

}

else{
    echo "failed to get data from database.";
}
mysqli_close($conn);
?>
