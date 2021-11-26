<?php

$street_code=$_GET['street_code'];
$address=$_GET['address'];

$header = apache_request_headers();
if( isset($header["X-Forwarded-For"]) ) {
    $ip_addr = $header['X-Forwarded-For'];
} else {
    $ip_addr = $_SERVER['REMOTE_ADDR'];
}
$output=array();
	
exec("cd /home/ubuntu/workspace/ && python3 address.py $street_code \"$address\"",$output);
if($output[2]!="no reuslt"){
	echo "{";
	    echo "\"status\":\"OK\",";
            echo "\"result\":";
                echo "[";
                    echo "{";
                        echo "\"x\":\"$output[2]\", \"y\":\"$output[3]\", \"main_no\":\"$output[5]\", \"additional_no\":\"$output[6]\"";
                    echo "}";
                echo "]";
        echo "}";
}else{
	echo "$output[2]";
}


?>
