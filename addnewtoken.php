<?php
define('HOST','mysql.hostinger.ru');
define('USER','u814414719_admin');
define('PASS','aru16431');
define('DB','u814414719_qrdb');
$con = mysqli_connect(HOST,USER,PASS, DB);

$secretkey = $_REQUEST['secretkey'];
$token = $_REQUEST['token'];

$success = array();

$id = 0;

$sql = "SELECT MAX(id) FROM qrtable;";
$result = mysqli_query($con, $sql);

if (mysqli_num_rows($result) > 0) {
           	while ($row = mysqli_fetch_array($result)) {
                	$id = $row['MAX(id)']+1;
            }
	}

if(!empty($secretkey) && !empty($token)){
	$sql1 = "SELECT * FROM qrtable WHERE token='$token';";
        $result1 = mysqli_query($con, $sql1);
        if (mysqli_num_rows($result1) > 0) {
           	$success["success"] = 0;
		$success["message"] = "token already exists";
            
	}else{
		$sql2 = "INSERT INTO qrtable (id, secretkey, token) VALUES ('$id','$secretkey','$token');";
        	$result2 = mysqli_query($con, $sql2);
        	if (mysqli_num_rows($result2) == true) {
            		$success["success"] = 1;
			        $success["message"] = "added new token";
            	}
	}
}else{
	$success["success"] = 2;
	$success["message"] = "empty data";
}
echo json_encode($success);
?>