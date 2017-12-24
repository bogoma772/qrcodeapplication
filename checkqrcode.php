<?php
define('HOST','mysql.hostinger.ru');
define('USER','u814414719_admin');
define('PASS','aru16431');
define('DB','u814414719_qrdb');
$con = mysqli_connect(HOST,USER,PASS, DB);

$sk = "gelicon";
$token = $_REQUEST['token'];

$key = "gelicon";

$id = 0;

if(!empty($token)){
	$sql1 = "SELECT * FROM qrtable WHERE token='$token';";
        $result1 = mysqli_query($con, $sql1);
        if (mysqli_num_rows($result1) > 0) {
		while($row = mysqli_fetch_array($result1))
    		{
                $header = [
                    'typ' => 'JWT',
                    'alg' => 'HS512'
                ];
                $header = json_encode($header);
                $header = base64_encode($header);

                $payload = [
                    'iss' => "gelicon",
                    'sk' => $row['secretkey']
                ];
                $payload = json_encode($payload);
                $payload = base64_encode($payload);

                $signature = hash_hmac('sha512', "$header.$payload", $key, true);
                $signature = base64_encode($signature);
                $token = "$header.$payload.$signature";

                $success = array(
                    "success" => $token
                );
                echo json_encode($success);
    		}

	}else{
            $success = array(
                "success" => "404"
            );
            echo json_encode($success);
	}
}else{
    $success = array(
        "success" => "400"
    );
    echo json_encode($success);
}
?>